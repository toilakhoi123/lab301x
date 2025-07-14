package com.khoi.lab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    private boolean confirmed;

    public Donation() {
    }

    public Donation(Account account, Campaign campaign, int amount, LocalDateTime donateTime) {
        this.account = account;
        this.campaign = campaign;
        this.amount = amount;
        this.donateTime = donateTime;
        this.confirmed = false;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    // ===== Helper methods =====

    public boolean isAnonymous() {
        return this.account == null;
    }

    @Override
    public String toString() {
        return "Donation [account=" + account + ", campaign=" + campaign + ", amount=" + amount + ", confirmed="
                + confirmed + "]";
    }
}