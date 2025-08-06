package com.khoi.lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.BlogDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for general mappings
 */
@Controller
public class GeneralController {
    private final DonationDAO donationDAO;
    private final AccountDAO accountDAO;
    private final BlogDAO blogDAO;

    /**
     * DAO Initiator
     * 
     * @param donationDAO
     */
    public GeneralController(DonationDAO donationDAO, AccountDAO accountDAO, BlogDAO blogDAO) {
        this.donationDAO = donationDAO;
        this.accountDAO = accountDAO;
        this.blogDAO = blogDAO;
    }

    /**
     * Login page
     * 
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        List<Campaign> campaigns = donationDAO.campaignList().stream()
                .filter(c -> c.getDonatedPercentageUncapped() < 100)
                .sorted((a, b) -> Integer.compare(b.getDonatedPercentageUncapped(), a.getDonatedPercentageUncapped()))
                .limit(3)
                .toList();
        List<BlogPost> blogPosts = blogDAO.listBlogPosts().stream().limit(2).toList();
        mav.addObject("campaigns", campaigns);
        mav.addObject("blogPosts", blogPosts);
        return mav;
    }

    /**
     * See currently logged in account's details
     * 
     * @param session
     * @return
     */
    @GetMapping("/edit-account")
    public ModelAndView sessionAccountEdit(HttpSession session) {
        ModelAndView mav = new ModelAndView("edit-account");
        mav.addObject("account", (Account) session.getAttribute("account"));
        return mav;
    }

    /**
     * Show an account's profile
     * (accountNotFound/notLoggedIn)
     * 
     * @param id
     * @return
     */
    @GetMapping("/account")
    public ModelAndView accountProfile(HttpSession session, @RequestParam(required = false) Long id) {
        Account account = (Account) session.getAttribute("account");

        // id or session account exists
        if (id != null || account != null) {
            account = (id != null)
                    ? accountDAO.accountFindWithId(id) // id provided
                    : accountDAO.accountFindWithId(account.getId()); // user logged in

            if (account == null) {
                ModelAndView mav = index();
                mav.addObject("accountNotFound", true);
                return mav;
            } else {
                for (Donation donation : account.getDonations()) {
                    donation.setTimeAgo(donation.getTimeAgo(donation.getDonateTime()));
                }
            }

            ModelAndView mav = new ModelAndView("account-details");
            mav.addObject("account", account);
            return mav;
        }

        ModelAndView mav = index();
        mav.addObject("notLoggedIn", account);
        return mav;
    }
}
