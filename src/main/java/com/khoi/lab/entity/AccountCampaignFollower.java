package com.khoi.lab.entity;

import jakarta.persistence.*;

/**
 * Intermediate entity to represent the many-to-many relationship
 * between Account and Campaign, with additional data.
 */
@Entity
@Table(name = "account_campaign_followers")
public class AccountCampaignFollower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "receive_notifications")
    private boolean receiveNotifications;

    public AccountCampaignFollower() {
    }

    public AccountCampaignFollower(Account account, Campaign campaign, boolean receiveNotifications) {
        this.account = account;
        this.campaign = campaign;
        this.receiveNotifications = receiveNotifications;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }
}
