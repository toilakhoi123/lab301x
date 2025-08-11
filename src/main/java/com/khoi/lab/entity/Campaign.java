package com.khoi.lab.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.khoi.lab.enums.CampaignStatus;

import jakarta.persistence.*;

/**
 * Represents a fundraising campaign entity.
 * This class maps to the "campaign" table in the database and manages all
 * campaign-related data,
 * including donations and goal tracking.
 */
@Entity
@Table(name = "campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private DonationReceiver receiver;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    private int goal;

    private int donatedPercentageCapped;
    private int donatedPercentageUncapped;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToMany(mappedBy = "campaign", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<Donation> donations = new ArrayList<>();

    /**
     * A list of accounts that are following this campaign.
     * This is the other side of the many-to-many relationship, mapped by the field
     * in the Account entity.
     */
    @ManyToMany(mappedBy = "followedCampaigns")
    private List<Account> followers = new ArrayList<>();

    /**
     * Default constructor for JPA.
     */
    public Campaign() {
    }

    /**
     * Constructor to create a new Campaign.
     * 
     * @param name        The name of the campaign.
     * @param receiver    The donation receiver associated with this campaign.
     * @param description A detailed description of the campaign.
     * @param imageUrl    The URL for the campaign's image.
     * @param goal        The fundraising goal in currency units.
     * @param startTime   The start date and time of the campaign.
     * @param endTime     The end date and time of the campaign.
     */
    public Campaign(String name, DonationReceiver receiver, String description,
            String imageUrl, int goal, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.receiver = receiver;
        this.status = CampaignStatus.CREATED;
        this.description = description;
        this.imageUrl = (imageUrl == null || imageUrl.isBlank())
                ? "https://www.shutterstock.com/image-vector/fundraising-giving-heart-symbol-money-600nw-2509445751.jpg"
                : imageUrl;
        this.goal = goal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.donations = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DonationReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(DonationReceiver receiver) {
        this.receiver = receiver;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Donation> getDonations() {
        return donations.stream()
                .sorted(Comparator.comparing(Donation::getDonateTime).reversed())
                .filter(d -> !d.isRefused())
                .collect(Collectors.toList());
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    public int getDonatedAmount() {
        return donations.stream()
                .filter(Donation::isConfirmed)
                .mapToInt(Donation::getAmount)
                .sum();
    }

    public double getDonatedPercentage() {
        int amount = getDonatedAmount();
        if (goal <= 0) {
            return 100;
        }

        BigDecimal amountBD = BigDecimal.valueOf(amount);
        BigDecimal goalBD = BigDecimal.valueOf(goal);
        BigDecimal percentage = amountBD
                .divide(goalBD, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);

        return Math.min(percentage.doubleValue(), 100.0);
    }

    public int getDonatedPercentageCapped() {
        return donatedPercentageCapped;
    }

    public void setDonatedPercentageCapped(int donatedPercentageCapped) {
        this.donatedPercentageCapped = donatedPercentageCapped;
    }

    public int getDonatedPercentageUncapped() {
        return donatedPercentageUncapped;
    }

    public void setDonatedPercentageUncapped(int donatedPercentageUncapped) {
        this.donatedPercentageUncapped = donatedPercentageUncapped;
    }

    // Getter and setter for followers
    public List<Account> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Account> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "Campaign [id=" + id + ", name=" + name + ", receiver=" + receiver + ", status=" + status + ", goal="
                + goal + "]";
    }
}
