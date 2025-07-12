package com.khoi.lab.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.khoi.lab.enums.CampaignStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private DonationReceiver receiver;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private int goal;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToMany(mappedBy = "campaign")
    private List<Donation> donations = new ArrayList<>();

    // Constructors, getters, setters

    public Campaign() {
    }

    public Campaign(String name, DonationReceiver receiver,
            String description, int goal, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.receiver = receiver;
        this.status = CampaignStatus.CREATED;
        this.description = description;
        this.goal = goal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.donations = new ArrayList<>();
    }

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
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    @Override
    public String toString() {
        return "Campaign [id=" + id + ", name=" + name + ", receiver=" + receiver + ", status=" + status + ", goal="
                + goal + "]";
    }
}
