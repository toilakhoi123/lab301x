package com.khoi.lab.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.CampaignStatus;
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

    @GetMapping("/dashboard")
    public ModelAndView dashboardPage(HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        // checks
        if (account == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // add datas
        int donationsWeekly = donationDAO.donationGetTotalRecent(TimeMinutes.WEEK.getMinutes());
        int donationsMonthly = donationDAO.donationGetTotalRecent(TimeMinutes.DAY.getMinutes() * 3);
        int campaignCompletedPercentage = donationDAO.campaignCompletedPercentage();
        int donationsPending = donationDAO.donationGetUnconfirmed();
        List<LocalDate> dates = donationDAO.getLast30Days();
        List<String> dateLabels = dates.stream()
                .map(d -> d.toString())
                .collect(Collectors.toList());
        List<Integer> donationAmounts = donationDAO.getDonationAmountsLast30Days();
        int anonymousDonations = donationDAO.donationGetAnonymous();
        int nonAnonymousDonations = donationDAO.donationGetNonAnonymous();

        // view
        ModelAndView mav = new ModelAndView("admin/dashboard");
        mav.addObject("donationsWeekly", donationsWeekly);
        mav.addObject("donationsMonthly", donationsMonthly);
        mav.addObject("campaignCompletedPercentage", campaignCompletedPercentage);
        mav.addObject("donationsPending", donationsPending);
        mav.addObject("labels1", dateLabels);
        mav.addObject("donations", donationAmounts);
        mav.addObject("groups", Arrays.asList(anonymousDonations, nonAnonymousDonations));
        return mav;
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
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }

    /**
     * Redirect to admin/manage-accounts page
     * 
     * @param param
     * @return
     */
    @GetMapping("/manage-accounts/quickedit")
    public ModelAndView accountsQuickEdit(HttpSession session) {
        return accountsManage(session);
    }

    /**
     * Handle account quick edit request (from manage-accounts action button)
     * (accountNotFound/accountEditSuccess)
     * 
     * @param id
     * @param isDisabled
     * @param isAdmin
     * @return
     */
    @PostMapping("/manage-accounts/quickedit")
    public ModelAndView accountsQuickEditRequest(HttpSession session, @RequestBody AccountEditRequest request) {
        Account account = accountDAO.accountFindWithId(request.id);

        if (account == null) {
            ModelAndView mav = accountsManage(session);
            mav.addObject("accountNotFound", true);
            return mav;
        }

        if (request.isDisabled != null) {
            account.setDisabled(request.isDisabled.equals("true"));
        }

        if (request.isAdmin != null) {
            account.setAdmin(request.isAdmin.equals("true"));
        }

        accountDAO.accountUpdate(account);
        ModelAndView mav = accountsManage(session);
        mav.addObject("accountEditSuccess", true);
        return mav;
    }

    /**
     * Show account edit page/redirect to manage-accounts page
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-accounts/edit")
    public ModelAndView accountsEdit(HttpSession session, @RequestParam(required = false) Long id) {
        if (id == null) {
            return accountsManage(session);
        }
        ModelAndView mav = new ModelAndView("admin/edit-account");
        mav.addObject("account", accountDAO.accountFindWithId(id));
        return mav;
    }

    /**
     * Handle account edit request
     * (accountNotFound/accountEditSuccess)
     * 
     * @param session
     * @param id
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param isAdmin
     * @param isDisabled
     * @return
     */
    @PostMapping("/manage-accounts/edit")
    public ModelAndView accountsEditRequest(
            HttpSession session,
            @RequestParam Long id,
            @RequestParam(required = false) String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String isAdmin,
            @RequestParam(required = false) String isDisabled) {
        Account account = accountDAO.accountFindWithId(id);

        if (account == null) {
            ModelAndView mav = accountsManage(session);
            mav.addObject("accountNotFound", true);
            return mav;
        }

        account.setUsername(username != null ? username : account.getUsername());
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);
        account.setPhoneNumber(phoneNumber);
        account.setAdmin(isAdmin != null);
        account.setDisabled(isDisabled != null);

        accountDAO.accountUpdate(account);

        ModelAndView mav = accountsManage(session);
        mav.addObject("accountEditSuccess", true);
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
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
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
    @PostMapping("/manage-campaigns/create")
    public ModelAndView campaignsCreate(
            HttpSession session,
            @RequestParam String name,
            @RequestParam int goal,
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
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
            ModelAndView mav = campaignsManage(session);
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
            ModelAndView mav = campaignsManage(session);
            mav.addObject("durationTooLong", true);
            return mav;
        }
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        // create campaign
        donationDAO.campaignCreate(name, donationReceiver, description, goal, startTime, endTime, imageUrl);

        // return view
        ModelAndView mav = campaignsManage(session);
        mav.addObject("campaignCreateSuccess", true);
        return mav;
    }

    /**
     * Handle edit campaign method
     * (notLoggedIn/notAuthorized)
     * 
     * @param session
     * @param id
     * @return
     */
    @GetMapping("/manage-campaigns/edit")
    public ModelAndView campaignsEdit(HttpSession session, @RequestParam Long id) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/edit-campaign");
        mav.addObject("campaign", donationDAO.campaignFindById(id));
        return mav;
    }

    /**
     * Handle campaign edit request
     * (campaignNotFound/campaignEditSuccess/campaignCannotBeEdited)
     * 
     * @param session
     * @param id
     * @param name
     * @param description
     * @param imageUrl
     * @param goal
     * @param receiverPhone
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping("/manage-campaigns/edit")
    public ModelAndView campaignsEditRequest(
            HttpSession session,
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String imageUrl,
            @RequestParam int goal,
            @RequestParam String receiverPhone,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignNotFound", true);
            return mav;
        }

        campaign.setImageUrl(imageUrl);
        campaign.setStartTime(startTime);
        campaign.setEndTime(endTime);

        // certain fields cannot be changed after opening
        if (campaign.getStatus() != CampaignStatus.CREATED) {
            donationDAO.campaignUpdate(campaign);
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignCannotBeEdited", true);
            return mav;
        }

        campaign.setName(name);
        campaign.setDescription(description);
        campaign.setGoal(goal);

        DonationReceiver donationReceiver = donationDAO.donationReceiverFindByPhoneNumber(receiverPhone);
        if (donationReceiver != null) {
            campaign.setReceiver(donationReceiver);
        }

        donationDAO.campaignUpdate(campaign);

        ModelAndView mav = campaignsManage(session);
        mav.addObject("campaignEditSuccess", true);
        return mav;
    }

    /**
     * Handle campaign endDate extension
     * (campaignNotFound/extensionTooLong/campaignExtendSuccess)
     * 
     * @param session
     * @param id
     * @param hours
     * @param days
     * @param weeks
     * @param months
     * @param years
     * @return
     */
    @PostMapping("/manage-campaigns/extend")
    public ModelAndView campaignsExtendRequest(
            HttpSession session,
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int hours,
            @RequestParam(defaultValue = "0") int days,
            @RequestParam(defaultValue = "0") int weeks,
            @RequestParam(defaultValue = "0") int months,
            @RequestParam(defaultValue = "0") int years) {
        Campaign campaign = donationDAO.campaignFindById(id);
        if (campaign == null) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignNotFound", true);
            return mav;
        }

        // check if extension too long (more than 3 years)
        Long extensionMinutes = TimeMinutes.YEAR.getMinutes() * years
                + TimeMinutes.MONTH.getMinutes() * months
                + TimeMinutes.WEEK.getMinutes() * weeks
                + TimeMinutes.DAY.getMinutes() * days
                + TimeMinutes.HOUR.getMinutes() * hours;
        if (extensionMinutes > (TimeMinutes.YEAR.getMinutes() * 3)) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("extensionTooLong", true);
            return mav;
        }
        donationDAO.campaignAddTimeMinutes(campaign, extensionMinutes);

        // view
        ModelAndView mav = campaignsManage(session);
        mav.addObject("campaignExtendSuccess", true);
        return mav;
    }

    /**
     * Close campaign handler
     * (notLoggedIn/notAuthorized/campaignNotFound/campaignAlreadyClosed/campaignCloseSuccess)
     * 
     * @param session
     * @param id
     * @return
     */
    @GetMapping("/manage-campaigns/close")
    public ModelAndView campaignsCloseRequest(HttpSession session, @RequestParam Long id) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignNotFound", true);
            return mav;
        }

        if (campaign.getStatus() == CampaignStatus.CLOSED) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignAlreadyClosed", true);
            return mav;
        }

        donationDAO.campaignChangeStatus(campaign, CampaignStatus.CLOSED);

        ModelAndView mav = campaignsManage(session);
        mav.addObject("campaignCloseSuccess", true);
        return mav;
    }

    /**
     * Delete campaign handler
     * (notLoggedIn/notAuthorized/campaignNotFound/campaignCannotBeDeleted/campaignDeleteSuccess)
     * 
     * @param session
     * @param id
     * @return
     */
    @GetMapping("/manage-campaigns/delete")
    public ModelAndView campaignsDeleteRequest(HttpSession session, @RequestParam Long id) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignNotFound", true);
            return mav;
        }

        if (campaign.getStatus() != CampaignStatus.CREATED) {
            ModelAndView mav = campaignsManage(session);
            mav.addObject("campaignCannotBeDeleted", true);
            return mav;
        }

        donationDAO.campaignDeleteById(id);

        ModelAndView mav = campaignsManage(session);
        mav.addObject("campaignDeleteSuccess", true);
        return mav;
    }

    /**
     * Manage campaigns' donations
     * (notLoggedIn/notAuthorized)
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-donations")
    public ModelAndView donationsManage(
            HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-donations");
        mav.addObject("donations", donationDAO.donationList());
        return mav;
    }

    /**
     * Handle donation confirm request
     * (donationConfirmSuccess)
     * 
     * @param request
     * @return
     */
    @PostMapping("/manage-donations/confirm")
    public ModelAndView donationsConfirmDonation(HttpSession session, @RequestBody DonationConfirmRequest request) {
        Donation donation = donationDAO.donationFindById(request.id);
        donationDAO.donationConfirm(donation);
        ModelAndView mav = donationsManage(session);
        mav.addObject("donationConfirmSuccess", true);
        return mav;
    }

    /**
     * Handle donation refuse request
     * (donationRefuseSuccess)
     * 
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/manage-donations/refuse")
    public ModelAndView donationsRefuseDonation(HttpSession session, @RequestBody DonationConfirmRequest request) {
        Donation donation = donationDAO.donationFindById(request.id);
        donationDAO.donationRefuse(donation);
        ModelAndView mav = donationsManage(session);
        mav.addObject("donationRefuseSuccess", true);
        return mav;
    }
}
