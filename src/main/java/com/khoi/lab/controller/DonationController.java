package com.khoi.lab.controller;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.config.Constants;
import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationPaymentCode;
import com.khoi.lab.enums.CampaignStatus;
import com.khoi.lab.enums.PaymentMethod;
import com.khoi.lab.service.StringService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.json.JSONObject;

@Controller
public class DonationController {
    private final DonationDAO donationDAO;
    private final AccountDAO accountDAO;

    /**
     * DAO Initiator
     * 
     * @param donationDAO
     * @param accountDAO
     */
    public DonationController(DonationDAO donationDAO, AccountDAO accountDAO) {
        this.donationDAO = donationDAO;
        this.accountDAO = accountDAO;
    }

    /**
     * View currently active campaigns
     * 
     * @param id
     * @return
     */
    @GetMapping("/campaigns")
    public ModelAndView campaignsPage() {
        List<Campaign> campaignsCreated = donationDAO.campaignFindByStatus(CampaignStatus.CREATED);
        List<Campaign> campaignsOpen = donationDAO.campaignFindByStatus(CampaignStatus.OPEN);
        List<Campaign> campaignsComplete = donationDAO.campaignFindByStatus(CampaignStatus.COMPLETE);
        List<Campaign> campaignsClosed = donationDAO.campaignFindByStatus(CampaignStatus.CLOSED);

        ModelAndView mav = new ModelAndView("campaigns");
        mav.addObject("campaignsCreated", campaignsCreated);
        mav.addObject("campaignsOpen", campaignsOpen);
        mav.addObject("campaignsComplete", campaignsComplete);
        mav.addObject("campaignsClosed", campaignsClosed);
        return mav;
    }

    /**
     * View a campaign's details
     * (campaignNotExist)
     * 
     * @param id
     * @return
     */
    @GetMapping("/campaigns/campaign")
    public ModelAndView campaignsViewDetail(@RequestParam Long id) {
        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignNotExist", true);
            return mav;
        }

        for (Donation donation : campaign.getDonations()) {
            donation.setTimeAgo(donation.getTimeAgo(donation.getDonateTime()));
        }

        // Simply retrieve the already sorted list from DAO
        List<AbstractMap.SimpleEntry<Account, Integer>> sortedList = donationDAO
                .campaignGetDonatorsAndDonatedAmount(campaign);

        ModelAndView mav = new ModelAndView("campaign-details");
        mav.addObject("campaign", campaign);
        mav.addObject("leaderboardList", sortedList);
        return mav;
    }

    /**
     * Returns donate page for a campaign
     * (campaignNotExist/campaignNotOpen/campaignClosed)
     * 
     * @param id
     * @return
     */
    @GetMapping("/campaigns/donate")
    public ModelAndView campaignsDonatePage(@RequestParam Long id) {
        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignNotExist", true);
            return mav;
        }

        if (campaign.getStatus() == CampaignStatus.CREATED) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignNotOpen", true);
            return mav;
        }

        if (campaign.getStatus() == CampaignStatus.CLOSED) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignClosed", true);
            return mav;
        }

        for (Donation donation : campaign.getDonations()) {
            donation.setTimeAgo(donation.getTimeAgo(donation.getDonateTime()));
        }

        ModelAndView mav = new ModelAndView("campaign-donate");
        mav.addObject("campaign", campaign);
        return mav;
    }

    /**
     * Handle campaign donate request
     * (campaignNotExist/accountNotExist/paymentCodeInvalid/donationSuccess)
     * 
     * @param entity
     * @return
     */
    @PostMapping("/campaigns/donate")
    public ModelAndView campaignsDonateRequest(@RequestBody String entity) {
        // parse json body
        JSONObject json = new JSONObject(entity);
        Long campaignId = json.getLong("campaignId");
        Long accountId = json.getLong("accountId");
        String code = json.getString("code");
        int amount = json.getInt("amount");

        // verification
        Campaign campaign = donationDAO.campaignFindById(campaignId);

        if (campaign == null) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignNotExist", true);
            return mav;
        }

        Account account = accountDAO.accountFindWithId(accountId);
        DonationPaymentCode donationPaymentCode = donationDAO.paymentCodeFindByCode(code);

        if (donationPaymentCode == null) {
            ModelAndView mav = campaignsPage();
            mav.addObject("paymentCodeInvalid", true);
            return mav;
        }

        // commit donation & exhaust payment code
        donationDAO.paymentCodeDeleteById(donationPaymentCode.getId());
        donationDAO.accountDonate(campaign, account, amount);

        // view
        ModelAndView mav = campaignsViewDetail(campaignId);
        mav.addObject("donationSuccess", true);
        return mav;
    }

    /**
     * Handle payment event
     * (paymentMethodUnavailable)
     * 
     * @param campaignId
     * @param amount
     * @param paymentMethod
     * @return
     */
    @GetMapping("/campaigns/donate/payment")
    public ModelAndView handleDonationPayment(
            @RequestParam Long campaignId,
            @RequestParam Long amount,
            @RequestParam PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case BANK:
                ModelAndView mav = new ModelAndView("payment/bank");

                String code = StringService.getAlphaNumericString(8);
                donationDAO.paymentCodeCreate(code);

                String description = new StringBuilder("QUYEN GOP ")
                        .append(code)
                        .toString();
                String qr = "https://qr.sepay.vn/img?acc=BANK_ACCOUNT_NUMBER&bank=BANK_NAME&amount=AMOUNT&des=DESCRIPTION"
                        .replace("BANK_ACCOUNT_NUMBER", Constants.BANK_ACCOUNT_NUMBER)
                        .replace("BANK_NAME", Constants.BANK_NAME)
                        .replace("AMOUNT", amount.toString())
                        .replace("DESCRIPTION", description);

                mav.addObject("qr", qr);
                mav.addObject("campaign", donationDAO.campaignFindById(campaignId));
                mav.addObject("bankAccountOwner", Constants.BANK_ACCOUNT_OWNER);
                mav.addObject("bankAccountNumber", Constants.BANK_ACCOUNT_NUMBER);
                mav.addObject("bankName", Constants.BANK_NAME);
                mav.addObject("bankFullName", Constants.BANK_FULL_NAME);
                mav.addObject("code", code);
                mav.addObject("description", description);
                mav.addObject("amount", amount);
                mav.addObject("sepayAPIToken", Constants.SEPAY_API_TOKEN);

                return mav;
            case MOMO:
                break;
            case PAYPAL:
                break;
            case VNPAY:
                break;
        }

        return campaignsPage().addObject("paymentMethodUnavailable", paymentMethod);
    }
}
