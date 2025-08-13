package com.khoi.lab.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.BlogDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.data.DefaultRolePermissions;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.BlogPost;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.entity.Role;
import com.khoi.lab.enums.CampaignStatus;
import com.khoi.lab.enums.DonationStatus;
import com.khoi.lab.enums.TimeMinutes;
import com.khoi.lab.enums.UserPermission;
import com.khoi.lab.object.DonationConfirmRequest;
import com.khoi.lab.service.UserPermissionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for admin mappings
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AccountDAO accountDAO;
    private final DonationDAO donationDAO;
    private final BlogDAO blogDAO;
    private final UserPermissionService userPermissionService;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public AdminController(AccountDAO accountDAO, DonationDAO donationDAO, BlogDAO blogDAO,
            UserPermissionService userPermissionService) {
        this.accountDAO = accountDAO;
        this.donationDAO = donationDAO;
        this.blogDAO = blogDAO;
        this.userPermissionService = userPermissionService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboardPage(HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.VIEW_DASHBOARD)) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
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
        return new ModelAndView("admin/dashboard")
                .addObject("donationsWeekly", donationsWeekly)
                .addObject("donationsMonthly", donationsMonthly)
                .addObject("campaignCompletedPercentage", campaignCompletedPercentage)
                .addObject("donationsPending", donationsPending)
                .addObject("labels", dateLabels)
                .addObject("donations", donationAmounts)
                .addObject("groups", Arrays.asList(anonymousDonations, nonAnonymousDonations));
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_USERS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-accounts");
        mav.addObject("accounts", accountDAO.accountList());
        mav.addObject("roles", accountDAO.roleList());
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
        mav.addObject("roles", accountDAO.roleList());
        return mav;
    }

    /**
     * Handle account edit request
     * (accountNotFound/accountEditSuccess/notAuthorized)
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
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String isDisabled) {
        Account account = accountDAO.accountFindWithId(id);

        if (account == null) {
            ModelAndView mav = accountsManage(session);
            mav.addObject("accountNotFound", true);
            return mav;
        }

        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_USERS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        if (roleName != null) {
            Role role = accountDAO.roleFindByRoleName(roleName);
            account.setRole(role);
        }

        account.setUsername(username != null ? username : account.getUsername());
        account.setFirstName(firstName != null ? firstName : account.getFirstName());
        account.setLastName(lastName != null ? lastName : account.getLastName());
        account.setEmail(email != null ? email : account.getEmail());
        account.setPhoneNumber(phoneNumber != null ? phoneNumber : account.getPhoneNumber());
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-campaigns");
        mav.addObject("campaigns", donationDAO.campaignList());
        return mav;
    }

    /**
     * Show campaign statistics
     * (notLoggedIn/notAuthorized/campaignNotFound)
     * 
     * @param session
     * @param id
     * @return
     */
    @GetMapping("/manage-campaigns/statistics")
    public ModelAndView campaignsViewCampaignStatistics(HttpSession session,
            @RequestParam(name = "campaign") Long id) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        Campaign campaign = donationDAO.campaignFindById(id);
        if (campaign == null) {
            return campaignsManage(session)
                    .addObject("campaignNotFound", true);
        }

        // calculate general stats
        int donationCount = campaign.getDonations().size();
        int donorCount = campaign.getDonations().stream()
                .map(donation -> donation.getAccount())
                .distinct().toList().size();
        int campaignProgress = campaign.getDonatedPercentageCapped();
        int donatedAmount = campaign.getDonatedAmount();
        Long daysLeft = campaign.getDaysLeft();
        long anonymousDonations = campaign.getDonations().stream().filter(d -> d.isAnonymous()).count();
        long nonAnonymousDonations = campaign.getDonations().size() - anonymousDonations;
        long pendingDonations = campaign.getDonations().stream().filter(d -> d.getStatus() == DonationStatus.PENDING)
                .count();
        long confirmedDonations = campaign.getDonations().stream()
                .filter(d -> d.getStatus() == DonationStatus.CONFIRMED).count();
        long refusedDonations = campaign.getDonations().size() - pendingDonations - confirmedDonations;

        // calculate donation history stats
        int numberOfDays = 10;
        List<LocalDate> dates = donationDAO.getLastXDays(numberOfDays);
        List<String> dateLabels = dates.stream()
                .map(d -> d.toString())
                .collect(Collectors.toList());
        List<Integer> donationAmounts = donationDAO.getDonationAmountsLastXDays(numberOfDays, campaign.getId());

        System.out.println("dateLabels:");
        for (String dateLabel : dateLabels) {
            System.out.println(dateLabel);
        }

        System.out.println("donationAmounts:");
        for (Integer donationAmount : donationAmounts) {
            System.out.println(donationAmount);
        }

        return new ModelAndView("admin/campaign-stats")
                .addObject("campaign", campaign)
                .addObject("donationCount", donationCount)
                .addObject("donorCount", donorCount)
                .addObject("campaignProgress", campaignProgress)
                .addObject("donatedAmount", donatedAmount)
                .addObject("donationGoal", campaign.getGoal())
                .addObject("daysLeft", daysLeft)
                .addObject("anonymousDonations", anonymousDonations)
                .addObject("nonAnonymousDonations", nonAnonymousDonations)
                .addObject("pendingDonations", pendingDonations)
                .addObject("confirmedDonations", confirmedDonations)
                .addObject("refusedDonations", refusedDonations)
                .addObject("dateLabels", dateLabels)
                .addObject("donationAmounts", donationAmounts);
    }

    /**
     * Handles campaign create request
     * (notLoggedIn/notAuthorized/donationReceiverNotFound/durationTooLong/campaignCreateSuccess)
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.CREATE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // edit campaign
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // close campaign
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // delete campaign
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_DONATIONS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-donations");
        mav.addObject("donations", donationDAO.donationList(true));
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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_DONATIONS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

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
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_DONATIONS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        Donation donation = donationDAO.donationFindById(request.id);
        donationDAO.donationRefuse(donation);
        ModelAndView mav = donationsManage(session);
        mav.addObject("donationRefuseSuccess", true);
        return mav;
    }

    /**
     * Handle donation status reset request
     * (donationResetSuccess)
     * 
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/manage-donations/reset")
    public ModelAndView donationsResetDonation(HttpSession session, @RequestBody DonationConfirmRequest request) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_DONATIONS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        Donation donation = donationDAO.donationFindById(request.id);
        donationDAO.donationReset(donation);
        ModelAndView mav = donationsManage(session);
        mav.addObject("donationRefuseSuccess", true);
        return mav;
    }

    /**
     * Handle create new donation receiver request
     * (donationReceiverPhoneNumberExists/donationReceiverCreateSuccess)
     * 
     * @param session
     * @param name
     * @param phoneNumber
     * @return
     */
    @PostMapping("/manage-donations/create-receiver")
    public ModelAndView donationReceiverCreate(
            HttpSession session,
            @RequestParam String name,
            @RequestParam String phoneNumber) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_CAMPAIGNS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        DonationReceiver donationReceiver = donationDAO.donationReceiverFindByPhoneNumber(phoneNumber);
        if (donationReceiver != null) {
            return campaignsManage(session).addObject("donationReceiverPhoneNumberExists", true);
        }
        donationDAO.donationReceiverCreate(name, phoneNumber);
        return campaignsManage(session).addObject("donationReceiverCreateSuccess", true);
    }

    /**
     * Shows the manage-blog interface
     * 
     * @param session
     * @return
     */
    @GetMapping("/manage-blogs")
    public ModelAndView blogsManage(
            HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_BLOGS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-blogs");
        mav.addObject("blogPosts", blogDAO.listBlogPosts());
        return mav;
    }

    /**
     * Create blog post
     * (blogPostCreateSuccess/notLoggedIn/notAuthorized)
     * 
     * @param session
     * @param title
     * @param description
     * @param imageUrl
     * @return
     */
    @PostMapping("/manage-blogs/create")
    public ModelAndView blogCreate(
            HttpSession session,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String imageUrl) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new GeneralController(donationDAO, accountDAO, blogDAO).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.CREATE_BLOGS)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        sessionAccount = accountDAO.accountFindWithId(sessionAccount.getId());
        blogDAO.createBlogPost(sessionAccount, imageUrl, title, description);
        return blogsManage(session).addObject("blogPostCreateSuccess", true);
    }

    /**
     * Handles the GET request for editing a blog post, displaying the edit form.
     * 
     * @param session The HTTP session to check for a logged-in user.
     * @param id      The ID of the blog post to be edited.
     * @return A ModelAndView for the edit blog page.
     */
    @GetMapping("/manage-blogs/edit")
    public ModelAndView blogEditRequest(HttpSession session, @RequestParam Long id) {
        // --- Permission Checks ---
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_BLOGS)) {
            ModelAndView mav = new ModelAndView("admin/dashboard");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // --- Fetch and display blog post data ---
        BlogPost blogPost = blogDAO.findBlogPostById(id);

        if (blogPost == null) {
            // Blog post not found, redirect back to the manage blogs page.
            ModelAndView mav = new ModelAndView("redirect:/admin/manage-blogs");
            mav.addObject("blogPostNotFound", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/edit-blog");
        mav.addObject("blogPost", blogPost);
        return mav;
    }

    /**
     * Handles the POST request from the edit form to update a blog post.
     * 
     * @param session  The HTTP session for permission checks.
     * @param blogPost The BlogPost object bound from the form data.
     * @return A ModelAndView to redirect back to the manage blogs page with a
     *         status message.
     */
    @PostMapping("/manage-blogs/edit")
    public ModelAndView blogEditPostRequest(HttpSession session, @RequestParam Long id, @RequestParam String title,
            @RequestParam String description, @RequestParam(required = false) String imageUrl) {
        // --- Permission Checks ---
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_BLOGS)) {
            ModelAndView mav = new ModelAndView("admin/dashboard");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        BlogPost blogPost = blogDAO.findBlogPostById(id);
        if (blogPost == null) {
            ModelAndView mav = new ModelAndView("redirect:/admin/manage-blogs");
            mav.addObject("blogPostNotFound", true);
            return mav;
        }

        blogPost.setTitle(title);
        blogPost.setDescription(description);
        blogPost.setImageUrl(imageUrl != null ? imageUrl : blogPost.getImageUrl());
        blogDAO.updateBlogPost(blogPost);

        // compose view
        ModelAndView mav = new ModelAndView("redirect:/admin/manage-blogs");
        mav.addObject("blogUpdateSuccess", true);
        return mav;
    }

    /**
     * Handles the GET request for deleting a blog post.
     * (notLoggedIn/notAuthorized/blogPostNotFound/blogDeleteSuccess)
     * 
     * @param session The HTTP session to check for a logged-in user.
     * @param id      The ID of the blog post to be deleted.
     * @return A ModelAndView to redirect back to the manage blogs page with a
     *         status message.
     */
    @GetMapping("/manage-blogs/delete")
    public ModelAndView blogDeleteRequest(HttpSession session, @RequestParam Long id) {
        // --- Permission Checks ---
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            // Redirect to a login or index page if the user is not logged in.
            // You'll need to instantiate a GeneralController or a similar class to get the
            // correct view.
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_BLOGS)) {
            // Redirect to the dashboard with an authorization error if the user lacks
            // permission.
            ModelAndView mav = new ModelAndView("admin/dashboard");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // --- Deletion Logic ---
        BlogPost blogPost = blogDAO.findBlogPostById(id);

        if (blogPost == null) {
            ModelAndView mav = new ModelAndView("redirect:/admin/manage-blogs");
            mav.addObject("blogPostNotFound", true);
            return mav;
        }

        blogDAO.deleteBlogPostById(id);

        ModelAndView mav = new ModelAndView("redirect:/admin/manage-blogs");
        mav.addObject("blogDeleteSuccess", true);
        return mav;
    }

    /**
     * Mapping to show manage-roles page
     * (notLoggedIn/notAuthorized)
     *
     * @param session
     * @return
     */
    @GetMapping("/manage-roles")
    public ModelAndView rolesManage(
            HttpSession session) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = (new GeneralController(donationDAO, accountDAO, blogDAO)).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_ROLES)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/manage-roles");
        mav.addObject("roles", accountDAO.roleList());
        mav.addObject("permissions", UserPermission.values());
        return mav;
    }

    /**
     * Handle role create request
     * (notLoggedIn/notAuthorized/roleCreateSuccess)
     *
     * @param session
     * @param roleName
     * @param powerLevel - New parameter to handle the power level from the form.
     * @return
     */
    @PostMapping("/manage-roles/create")
    public ModelAndView rolesCreate(
            HttpSession session,
            @RequestParam String roleName,
            @RequestParam int powerLevel) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new GeneralController(donationDAO, accountDAO, blogDAO).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.CREATE_ROLES)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // New check: A user cannot create a role with a power level >= their own role's
        // power level.
        if (sessionAccount.getRole().getPowerLevel() <= powerLevel) {
            // Redirect to the manage roles page with an error message.
            return rolesManage(session).addObject("roleCreateFailure", true);
        }

        accountDAO.roleCreate(
                roleName.replaceAll(" ", "_").toLowerCase(),
                DefaultRolePermissions.getPermissionsForRole("user"),
                powerLevel); // Now using the powerLevel from the form.
        return rolesManage(session).addObject("roleCreateSuccess", true);
    }

    /**
     * Handle role update request
     * (notLoggedIn/notAuthorized)
     *
     * @param session
     * @param formData
     * @return
     */
    @PostMapping("/manage-roles/update")
    public ModelAndView updateRoles(HttpSession session, @RequestParam MultiValueMap<String, String> formData) {
        // --- Permission Checks ---
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new GeneralController(donationDAO, accountDAO, blogDAO).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_ROLES)) {
            ModelAndView mav = new ModelAndView("admin/dashboard");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // Map to hold updated role data before saving
        Map<Long, Role> rolesToUpdate = new HashMap<>();

        // Iterate through formData to find all roles being updated
        for (String paramName : formData.keySet()) {
            if (paramName.startsWith("permissions[") || paramName.startsWith("roleName[")
                    || paramName.startsWith("powerLevel[")) {
                try {
                    String roleIdStr = paramName.substring(paramName.indexOf('[') + 1, paramName.indexOf(']'));
                    Long roleId = Long.parseLong(roleIdStr);

                    // Initialize role object for update if it doesn't exist in the map
                    if (!rolesToUpdate.containsKey(roleId)) {
                        Role existingRole = accountDAO.roleFindById(roleId);
                        if (existingRole == null) {
                            System.out.println("Role with ID " + roleId + " not found. Skipping update.");
                            continue;
                        }

                        // New check: A user cannot update a role with a power level >= their own.
                        if (existingRole.getPowerLevel() >= sessionAccount.getRole().getPowerLevel()) {
                            System.out.println("Attempt to update a role with equal or higher power level. Skipping.");
                            continue;
                        }

                        rolesToUpdate.put(roleId, existingRole);
                    }

                    // Update the role object based on the form data
                    if (paramName.startsWith("roleName[")) {
                        rolesToUpdate.get(roleId).setRoleName(formData.getFirst(paramName));
                    } else if (paramName.startsWith("powerLevel[")) {
                        rolesToUpdate.get(roleId).setPowerLevel(Integer.parseInt(formData.getFirst(paramName)));
                    } else if (paramName.startsWith("permissions[")) {
                        List<String> permissionStrings = formData.get(paramName);
                        List<UserPermission> updatedPermissions = new ArrayList<>();

                        if (permissionStrings != null) {
                            for (String permName : permissionStrings) {
                                try {
                                    updatedPermissions.add(UserPermission.valueOf(permName));
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid permission name: " + permName + ". Skipping.");
                                }
                            }
                        }
                        rolesToUpdate.get(roleId).setPermissions(updatedPermissions);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing role ID or permissions: " + e.getMessage());
                }
            }
        }

        // Save all the updated roles
        for (Role role : rolesToUpdate.values()) {
            accountDAO.roleUpdate(role);
        }

        return new ModelAndView("redirect:/admin/manage-roles").addObject("roleUpdateSuccess", true);
    }

    /**
     * Handle role delete request
     * (notLoggedIn/notAuthorized)
     * * @param session
     * 
     * @param id
     * @return
     */
    @GetMapping("/manage-roles/delete")
    public ModelAndView deleteRole(
            HttpSession session,
            @RequestParam long id) {
        // permission checks
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            ModelAndView mav = new GeneralController(donationDAO, accountDAO, blogDAO).index();
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!userPermissionService.hasPermission(sessionAccount, UserPermission.MANAGE_ROLES)) {
            ModelAndView mav = dashboardPage(session);
            mav.addObject("notAuthorized", true);
            return mav;
        }

        // find role
        Role roleToDelete = accountDAO.roleFindById(id);
        if (roleToDelete == null) {
            return rolesManage(session).addObject("roleDeleteFailure", true);
        }

        // New check: A user cannot delete a role with a power level >= their own.
        if (roleToDelete.getPowerLevel() >= sessionAccount.getRole().getPowerLevel()) {
            return rolesManage(session).addObject("roleDeleteFailure", true);
        }

        accountDAO.roleDeleteById(id);
        return rolesManage(session).addObject("roleDeleteSuccess", true);
    }
}
