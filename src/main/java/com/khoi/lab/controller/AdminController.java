package com.khoi.lab.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.TimeMinutes;
import com.khoi.lab.object.AccountEditRequest;
import com.khoi.lab.object.DonationConfirmRequest;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AccountDAO accountDAO;
    private final DonationDAO donationDAO;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public AdminController(AccountDAO accountDAO, DonationDAO donationDAO) {
        this.accountDAO = accountDAO;
        this.donationDAO = donationDAO;
    }

    /**
     * Manage accounts page
     * (notLoggedIn/notAuthorized)
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-accounts")
    public ModelAndView accountsManage(
            HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }

    /**
     * Redirect to admin/accounts page
     * 
     * @param param
     * @return
     */
    @GetMapping("/manage-accounts/edit")
    public ModelAndView accountEdit() {
        ModelAndView mav = new ModelAndView("admin/manage-accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }

    /**
     * Handle account edit request
     * 
     * @param id
     * @param isDisabled
     * @param isAdmin
     * @return
     */
    @PostMapping("/manage-accounts/edit")
    public ModelAndView accountsEditRequest(@RequestBody AccountEditRequest request) {
        Account account = accountDAO.accountFindWithId(request.id);

        if (request.isDisabled != null) {
            account.setDisabled(request.isDisabled.equals("true"));
        }

        if (request.isAdmin != null) {
            account.setAdmin(request.isAdmin.equals("true"));
        }

        accountDAO.accountUpdate(account);

        ModelAndView mav = new ModelAndView("admin/manage-accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }

    /**
     * Manage campaigns
     * (notLoggedIn/notAuthorized)
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-campaigns")
    public ModelAndView campaignsManage(
            HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-campaigns");
        mav.addObject("campaigns", donationDAO.campaignList());
        return mav;
    }

    /**
     * Handles campaign create request
     * (donationReceiverNotFound, durationTooLong/campaignCreateSuccess)
     * 
     * @param name
     * @param goal
     * @param description
     * @return
     */
    @PostMapping("/manage-campaigns/create-campaign")
    public ModelAndView campaignsCreate(@RequestParam String name,
            @RequestParam int goal,
            @RequestParam String description,
            @RequestParam String receiverPhone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(defaultValue = "0") int hours,
            @RequestParam(defaultValue = "0") int days,
            @RequestParam(defaultValue = "0") int weeks,
            @RequestParam(defaultValue = "0") int months,
            @RequestParam(defaultValue = "0") int years) {
        // check if donation receiver found
        DonationReceiver donationReceiver = donationDAO.donationReceiverFindByPhoneNumber(receiverPhone);
        if (donationReceiver == null) {
            ModelAndView mav = new ModelAndView("admin/manage-campaigns");
            mav.addObject("campaigns", donationDAO.campaignList());
            mav.addObject("donationReceiverNotFound", true);
            return mav;
        }

        // check if time too long (more than 3 years)
        Long durationMinutes = TimeMinutes.YEAR.getMinutes() * years
                + TimeMinutes.MONTH.getMinutes() * months
                + TimeMinutes.WEEK.getMinutes() * weeks
                + TimeMinutes.DAY.getMinutes() * days
                + TimeMinutes.HOUR.getMinutes() * hours;
        if (durationMinutes > (TimeMinutes.YEAR.getMinutes() * 3)) {
            ModelAndView mav = new ModelAndView("admin/manage-campaigns");
            mav.addObject("campaigns", donationDAO.campaignList());
            mav.addObject("durationTooLong", true);
            return mav;
        }
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        // create campaign
        donationDAO.campaignCreate(name, donationReceiver, description, goal, startTime, endTime);

        // return view
        ModelAndView mav = new ModelAndView("admin/manage-campaigns");
        mav.addObject("campaigns", donationDAO.campaignList());
        mav.addObject("campaignCreateSuccess", true);
        return mav;
    }

    /**
     * Manage campaigns' donations
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-donations")
    public ModelAndView donationsManage(
            HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-donations");
        mav.addObject("donations", donationDAO.donationList());
        return mav;
    }

    /**
     * Handle donation confirm request
     * 
     * @param request
     * @return
     */
    @PostMapping("/manage-donations/confirm")
    public ModelAndView donationsConfirmDonation(@RequestBody DonationConfirmRequest request) {
        Donation donation = donationDAO.donationFindById(request.id);

        donationDAO.donationConfirm(donation);

        ModelAndView mav = new ModelAndView("admin/manage-donations");
        mav.addObject("donations", donationDAO.donationList());
        return mav;
    }
}
