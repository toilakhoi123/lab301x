package com.khoi.lab.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationPaymentCode;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.CampaignStatus;

/**
 * Data Access Object for Accounts
 */
public interface DonationDAO {
    /**
     * Generate random donations for test data
     * 
     * @param campaigns
     * @param accounts
     * @param minDonationsPerCampaign
     * @param maxDonationsPerCampaign
     * @param minAmount
     * @param maxAmount
     * @param amountInterval
     * @param maxDaysAgo
     * @return
     */
    List<Donation> generateRandomDonations(List<Campaign> campaigns, List<Account> accounts,
            int minDonationsPerCampaign, int maxDonationsPerCampaign, long minAmount, long maxAmount,
            long amountInterval, int maxDaysAgo);

    /**
     * Initiate test data
     */
    void initiate();

    /**
     * Create donation receiver (person/organization)
     * 
     * @param name
     * @param phoneNumber
     * @return
     */
    DonationReceiver donationReceiverCreate(String name, String phoneNumber);

    /**
     * Save donation receiver
     * 
     * @param donationReceiver
     * @return
     */
    DonationReceiver donationReceiverSave(DonationReceiver donationReceiver);

    /**
     * Update donation receiver
     * 
     * @param donationReceiver
     * @return
     */
    DonationReceiver donationReceiverUpdate(DonationReceiver donationReceiver);

    /**
     * Find donation receiver by phone number
     * 
     * @param phoneNumber
     * @return
     */
    DonationReceiver donationReceiverFindByPhoneNumber(String phoneNumber);

    /**
     * Create a new campaign for a designated donation receiver
     * 
     * @param name
     * @param donationReceiver
     * @param description
     * @param goal
     * @param startTime
     * @param endTime
     * @return
     */
    Campaign campaignCreate(String name, DonationReceiver donationReceiver, String description, int goal,
            LocalDateTime startTime, LocalDateTime endTime, String imageUrl);

    /**
     * Find campaign by id
     * 
     * @param id
     * @return
     */
    Campaign campaignFindById(Long id);

    /**
     * Add minutes to campaign time
     * 
     * @param minutes
     * @return
     */
    Campaign campaignAddTimeMinutes(Campaign campaign, Long minutes);

    /**
     * Get donated amount of cash
     * 
     * @param campaign
     * @return
     */
    int campaignGetDonatedAmount(Campaign campaign);

    /**
     * Get percentage of money donated/goal
     * 
     * @param campaign
     * @return
     */
    double campaignGetDonatedPercentage(Campaign campaign);

    /**
     * Get donations of a campaign, optionally exclude confirmed donations
     * 
     * @param campaign
     * @return
     */
    List<Donation> campaignGetDonations(Campaign campaign, boolean excludeConfirmed);

    /**
     * Get donators and sum of money they donated to the campaign
     * 
     * @param campaign
     * @return
     */
    List<AbstractMap.SimpleEntry<Account, Integer>> campaignGetDonatorsAndDonatedAmount(Campaign campaign);

    /**
     * Change campaign status
     * 
     * @param campaign
     * @param status
     * @return
     */
    Campaign campaignChangeStatus(Campaign campaign, CampaignStatus status);

    /**
     * Find campaigns by status
     * 
     * @param status
     * @return
     */
    List<Campaign> campaignFindByStatus(CampaignStatus status);

    /**
     * Find campaign by donation receiver's phone number
     * 
     * @param phoneNumber
     * @return
     */
    Campaign campaignFindByDonationReceiverPhoneNumber(String phoneNumber);

    /**
     * Campaign delete by Id
     * 
     * @param id
     */
    void campaignDeleteById(Long id);

    /**
     * Save campaign
     * 
     * @param campaign
     * @return
     */
    Campaign campaignSave(Campaign campaign);

    /**
     * Update campaign
     * 
     * @param campaign
     * @return
     */
    Campaign campaignUpdate(Campaign campaign);

    /**
     * Return a list of all campaigns
     * 
     * @return
     */
    List<Campaign> campaignList();

    /**
     * Confirm a donation manually
     * 
     * @param donation
     * @return
     */
    Donation donationConfirm(Donation donation);

    /**
     * Refuse a donation manually
     * 
     * @param donation
     * @return
     */
    Donation donationRefuse(Donation donation);

    /**
     * Reset donation status manually
     * 
     * @param donation
     * @return
     */
    Donation donationReset(Donation donation);

    /**
     * Save a donation
     * 
     * @param donation
     * @return
     */
    Donation donationSave(Donation donation);

    /**
     * Update a donation
     */
    Donation donationUpdate(Donation donation);

    /**
     * Return all donations present
     * 
     * @return
     */
    List<Donation> donationList(boolean includeRefused);

    /**
     * Find and return a donation with matching id
     * 
     * @param id
     * @return
     */
    Donation donationFindById(Long id);

    /**
     * Create a payment code
     * 
     * @param code
     * @return
     */
    DonationPaymentCode paymentCodeCreate(String code);

    /**
     * Find payment code by its code
     * 
     * @param code
     * @return
     */
    DonationPaymentCode paymentCodeFindByCode(String code);

    /**
     * Find payment code by its id
     * 
     * @param id
     * @return
     */
    DonationPaymentCode paymentCodeFindById(Long id);

    /**
     * Save the payment code to the database
     * 
     * @param donationPaymentCode
     * @return
     */
    DonationPaymentCode paymentCodeSave(DonationPaymentCode donationPaymentCode);

    /**
     * Delete payment code by ID
     * 
     * @param id
     */
    void paymentCodeDeleteById(Long id);

    /**
     * Get donations made by an account
     * 
     * @param account
     * @return
     */
    List<Donation> accountGetDonations(Account account);

    /**
     * Account donate to campaign
     * 
     * @param campaign
     * @param account
     * @param amount
     * @return
     */
    Donation accountDonate(Campaign campaign, Account account, int amount);

    /**
     * Get the amount of cash donated in the last X minutes (for weekly/monthly)
     * 
     * @param timeMinutes
     * @return
     */
    int donationGetTotalRecent(Long timeMinutes);

    /**
     * Get the percentage of completed/closed campaigns
     * 
     * @return
     */
    int campaignCompletedPercentage();

    /**
     * Get the amount of unconfirmed donations
     * 
     * @return
     */
    int donationGetUnconfirmed();

    /**
     * Get the amount donated on day [date]
     * 
     * @param date
     * @return
     */
    int donationGetAmountOnDay(LocalDate date);

    /**
     * Get the amount donated on day [date], but for a specific campaign
     * 
     * @param date
     * @param campaignId
     * @return
     */
    int donationGetAmountOnDay(LocalDate date, Long campaignId);

    /**
     * Get x-axis of bar chart donations for the last X days
     * 
     * @param days
     * @return
     */
    List<LocalDate> getLastXDays(int days);

    /**
     * Get y-axis of bar chart donations (amount donated) for the last X days
     * 
     * @param days
     * @return
     */
    List<Integer> getDonationAmountsLastXDays(int days);

    /**
     * Get y-axis of bar chart donations (amount donated) for the last 30 days, but
     * for a specific campaign
     * 
     * @param days
     * @param campaignId
     * @return
     */
    List<Integer> getDonationAmountsLastXDays(int days, Long campaignId);

    /**
     * Get x-axis of bar chart donations for the last 30 days
     * 
     * @return
     */
    List<LocalDate> getLast30Days();

    /**
     * Get y-axis of bar chart donations (amount donated) for the last 30 days
     * 
     * @return
     */
    List<Integer> getDonationAmountsLast30Days();

    /**
     * An utility to get both x and y axes of donation chart last 30 days
     * 
     * @return
     */
    Map<LocalDate, Integer> getDonationChartDataLast30Days();

    /**
     * Get the amount of anonymous donations
     * 
     * @return
     */
    int donationGetAnonymous();

    /**
     * Get the amount of non-anonymous donations
     * 
     * @return
     */
    int donationGetNonAnonymous();
}