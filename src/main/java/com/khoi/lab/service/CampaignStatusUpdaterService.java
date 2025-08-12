package com.khoi.lab.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.data.EmailTemplates;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.AccountCampaignFollower;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.enums.CampaignStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Campaign status updater service
 */
@Service
public class CampaignStatusUpdaterService {
    private final DonationDAO donationDAO;
    private final EmailSenderService senderService;

    public CampaignStatusUpdaterService(DonationDAO donationDAO, EmailSenderService senderService) {
        this.donationDAO = donationDAO;
        this.senderService = senderService;
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
            String eventName = "";

            // check change of status
            switch (campaign.getStatus()) {
                case CampaignStatus.CREATED:
                    if (now.isAfter(campaign.getStartTime()) || now.isEqual(campaign.getStartTime())) {
                        eventName = "CAMPAIGN_OPENED";
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.OPEN);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status CREATED -> OPEN");
                    }
                    break;
                case CampaignStatus.OPEN:
                    if (campaign.getDonatedAmount() >= campaign.getGoal()) {
                        eventName = "CAMPAIGN_COMPLETED";
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.COMPLETE);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status OPEN -> COMPLETE");
                    }
                    break;
                case CampaignStatus.COMPLETE:
                    if (now.isAfter(campaign.getEndTime()) || now.isEqual(campaign.getEndTime())) {
                        eventName = "CAMPAIGN_CLOSED";
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.CLOSED);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status COMPLETE -> CLOSED");
                    }
                    break;
                case CampaignStatus.CLOSED:
                    if ((now.isAfter(campaign.getStartTime()) || now.isEqual(campaign.getStartTime()))
                            && campaign.getDonatedPercentageCapped() < 100) {
                        eventName = "CAMPAIGN_REOPENED";
                        donationDAO.campaignChangeStatus(campaign, CampaignStatus.OPEN);
                        System.out.println("| [service:CampaignStatusUpdater] Campaign: " + campaign.getId()
                                + " updated status CLOSED -> OPEN");
                    }
                    break;
            }

            // skip the below if no events happened
            if (eventName == "")
                continue;

            // send notifications to subscribed accounts
            for (AccountCampaignFollower acf : campaign.getFollowers().stream()
                    .filter(acf_ -> acf_.isReceiveNotifications())
                    .collect(Collectors.toList())) {
                Account followerAccount = acf.getAccount();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                List<String> template = EmailTemplates.getEmailTemplateForEvent(eventName);
                if (template == null) {
                    System.out.println("| [service:CampaignStatusUpdater] Unrecognized event " + eventName
                            + ", skipping mail send.");
                    return;
                }
                String subject = template.get(0);
                String description = template.get(1)
                        .replace("{CAMPAIGN_NAME}", campaign.getName())
                        .replace("{CAMPAIGN_GOAL}", campaign.getGoal() + "đ")
                        .replace("{CAMPAIGN_END_TIME}", campaign.getEndTime().format(formatter))
                        .replace("{CAMPAIGN_LINK}", "http://localhost:8080/campaigns/campaign?id=" + campaign.getId());

                new Thread(() -> {
                    senderService.sendEmail(followerAccount.getEmail(), subject, description);
                }).start();

                System.out.println("| [service:CampaignStatusUpdater] User: " + followerAccount.getId()
                        + " received a notification via email!");
            }
        }
    }
}
