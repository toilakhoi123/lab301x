package com.khoi.lab.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.enums.CampaignStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Campaign status updater service
 */
@Service
public class CampaignStatusUpdaterService {
    private final DonationDAO donationDAO;

    public CampaignStatusUpdaterService(DonationDAO donationDAO) {
        this.donationDAO = donationDAO;
    }

    /**
     * Update campaign statuses
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateCampaignStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaigns = donationDAO.campaignList();

        System.out.println("| [service:CampaignStatusUpdater] Periodic checking campaign statuses...");

        for (Campaign campaign : campaigns) {
            switch (campaign.getStatus()) {
                case CampaignStatus.CREATED:
                    if (now.isAfter(campaign.getStartTime()) || now.isEqual(campaign.getStartTime())) {
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.OPEN);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status CREATED -> OPEN");
                    }
                    break;
                case CampaignStatus.OPEN:
                    if (campaign.getDonatedAmount() >= campaign.getGoal()) {
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.COMPLETE);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status OPEN -> COMPLETE");
                    }
                    break;
                case CampaignStatus.COMPLETE:
                    if (now.isAfter(campaign.getEndTime()) || now.isEqual(campaign.getEndTime())) {
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.CLOSED);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status COMPLETE -> CLOSED");
                    }
                    break;
                case CampaignStatus.CLOSED:
                    break;
            }
        }
    }
}
