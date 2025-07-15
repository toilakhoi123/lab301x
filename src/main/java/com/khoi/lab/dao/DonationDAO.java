package com.khoi.lab.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.CampaignStatus;

/**
 * Data Access Object for Accounts
 */
public interface DonationDAO {
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
    public Campaign campaignAddTimeMinutes(Campaign campaign, Long minutes);

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
    HashMap<Account, Integer> campaignGetDonatorsAndDonatedAmount(Campaign campaign);

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
    List<Donation> donationList();

    /**
     * Find and return a donation with matching id
     * 
     * @param id
     * @return
     */
    Donation donationFindById(Long id);

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
}
