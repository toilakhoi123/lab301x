package com.khoi.lab.entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.khoi.lab.enums.DonationStatus;

@Entity
@Table(name = "donation")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = true)
    private Account account; // optional

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private int amount;

    private LocalDateTime donateTime;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    private String timeAgo;

    public Donation() {
    }

    public Donation(Account account, Campaign campaign, int amount, LocalDateTime donateTime) {
        this.account = account;
        this.campaign = campaign;
        this.amount = amount;
        this.donateTime = donateTime;

        this.status = DonationStatus.PENDING;
    }

    public Long getId() {
        return id;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getDonateTime() {
        return donateTime;
    }

    public void setDonateTime(LocalDateTime donateTime) {
        this.donateTime = donateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    // ===== Helper methods ===== //

    public boolean isAnonymous() {
        return this.account == null;
    }

    public boolean isPending() {
        return this.status == DonationStatus.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == DonationStatus.CONFIRMED;
    }

    public boolean isRefused() {
        return this.status == DonationStatus.REFUSED;
    }

    public String getTimeAgo(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        if (duration.toMinutes() < 1) {
            return "just now";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + " minute(s) ago";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + " hour(s) ago";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + " day(s) ago";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    @Override
    public String toString() {
        return "Donation [account=" + account + ", campaign=" + campaign + ", amount=" + amount + ", status="
                + status + "]";
    }
}