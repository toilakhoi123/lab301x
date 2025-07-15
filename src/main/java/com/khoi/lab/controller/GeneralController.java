package com.khoi.lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;

import jakarta.servlet.http.HttpSession;

@Controller
public class GeneralController {
    private final DonationDAO donationDAO;

    /**
     * DAO Initiator
     * 
     * @param donationDAO
     */
    public GeneralController(DonationDAO donationDAO) {
        this.donationDAO = donationDAO;
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

        mav.addObject("campaigns", campaigns);
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
}
