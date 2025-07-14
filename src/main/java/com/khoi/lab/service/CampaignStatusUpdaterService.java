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

        // iterate
        for (Campaign campaign : campaigns) {
            boolean statusUpdated = false;

            // check status
            switch (campaign.getStatus()) {
                case CampaignStatus.CREATED:
                    if (now.isAfter(campaign.getStartTime()) || now.isEqual(campaign.getStartTime())) {
                        campaign.setStatus(CampaignStatus.OPEN);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status CREATED -> OPEN");
                        statusUpdated = true;
                    }
                    break;
                case CampaignStatus.OPEN:
                    if (campaign.getDonatedAmount() >= campaign.getGoal()) {
                        campaign.setStatus(CampaignStatus.COMPLETE);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status OPEN -> COMPLETE");
                        statusUpdated = true;
                    }
                    break;
                case CampaignStatus.COMPLETE:
                    if (now.isAfter(campaign.getEndTime()) || now.isEqual(campaign.getEndTime())) {
                        campaign.setStatus(CampaignStatus.CLOSED);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status COMPLETE -> CLOSED");
                        statusUpdated = true;
                    }
                    break;
                default:
                    break;
            }

            // status updated -> persist
            if (statusUpdated) {
                donationDAO.campaignUpdate(campaign);
            }
        }
    }
}
