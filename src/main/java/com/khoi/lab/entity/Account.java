package com.khoi.lab.entity;

import com.khoi.lab.enums.UserPermission;
import com.khoi.lab.service.CryptographyService;
import jakarta.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Account Entity
 */
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    // A single user now belongs to one role
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "is_disabled")
    private boolean isDisabled;

    /**
     * Blog post comments made by this account
     */
    @OneToMany(mappedBy = "account", cascade = { CascadeType.ALL })
    private List<BlogPostComment> blogPostComments;

    /**
     * Blog post that this account authors
     */
    @OneToMany(mappedBy = "author", cascade = { CascadeType.ALL })
    private List<BlogPost> blogPosts;

    /**
     * Date of last login attempt from the user
     */
    @Column(name = "last_login_date")
    private Date lastLoginDate;

    /**
     * Donations made by this account
     */
    @OneToMany(mappedBy = "account", cascade = { CascadeType.ALL })
    private List<Donation> donations = new ArrayList<>();

    /**
     * Campaigns that this account is following.
     * This is the corrected relationship using an intermediate entity.
     * The `orphanRemoval = true` attribute ensures that when a
     * AccountCampaignFollower entity is removed from this list, it is also
     * deleted from the database.
     */
    @OneToMany(mappedBy = "account", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<AccountCampaignFollower> followedCampaigns = new ArrayList<>();

    public Account() {
    }

    /**
     * Register constructor
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param password
     * @param role
     */
    public Account(String username, String firstName, String lastName, String email, String phoneNumber,
            String password, Role role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isDisabled = false;
        this.donations = new ArrayList<>();
        this.followedCampaigns = new ArrayList<>();
        setPassword(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return CryptographyService.decrypt(password);
    }

    public void setPassword(String password) {
        this.password = CryptographyService.encrypt(password);
    }

    // New helper method to check if the account has a specific permission
    public boolean hasPermission(UserPermission permission) {
        return this.role != null && this.role.getPermissions().contains(permission);
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
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

    public List<BlogPostComment> getBlogPostComments() {
        return blogPostComments;
    }

    public void setBlogPostComments(List<BlogPostComment> blogPostComments) {
        this.blogPostComments = blogPostComments;
    }

    public List<BlogPost> getBlogPosts() {
        return blogPosts;
    }

    public void setBlogPosts(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Campaign> getFollowedCampaigns() {
        return followedCampaigns.stream()
                .map(AccountCampaignFollower::getCampaign)
                .collect(Collectors.toList());
    }

    public List<Long> getFollowedCampaignIds() {
        return getFollowedCampaigns().stream()
                .map(Campaign::getId)
                .collect(Collectors.toList());
    }

    public List<AccountCampaignFollower> getAccountCampaignFollowers() {
        return followedCampaigns;
    }

    public void setAccountCampaignFollowers(List<AccountCampaignFollower> followedCampaigns) {
        this.followedCampaigns = followedCampaigns;
    }

    // Helper method to add a new followed campaign with notification preference
    public void addFollowedCampaign(Campaign campaign, boolean receiveNotifications) {
        AccountCampaignFollower follower = new AccountCampaignFollower(this, campaign, receiveNotifications);
        this.followedCampaigns.add(follower);
    }

    public void removeFollowedCampaign(Campaign campaign) {
        this.followedCampaigns
                .removeIf(acf -> acf.getCampaign().getId().equals(campaign.getId()));
    }

    // new helper
    public void addDonation(Donation donation) {
        this.donations.add(donation);
    }

    public boolean isFollowingCampaignId(Long id) {
        return getFollowedCampaignIds().contains(id);
    }

    /**
     * Finds and returns a single AccountCampaignFollower by campaign ID.
     * This method now uses a stream to safely find the element without modifying
     * the original list.
     * It returns an Optional to handle cases where no follower is found.
     *
     * @param campaignId The ID of the campaign to find the follower for.
     * @return An Optional containing the follower, or an empty Optional if not
     *         found.
     */
    public Optional<AccountCampaignFollower> getAccountCampaignFollowerById(Long campaignId) {
        return getAccountCampaignFollowers().stream()
                .filter(acf -> acf.getCampaign().getId().equals(campaignId))
                .findFirst();
    }

    /**
     * Checks if notifications are enabled for a given campaign ID.
     * This method now safely handles the Optional returned by
     * getAccountCampaignFollowerById.
     *
     * @param campaignId The ID of the campaign.
     * @return true if a follower is found and notifications are enabled, false
     *         otherwise.
     */
    public boolean isCampaignIdEnableNotifications(Long campaignId) {
        return getAccountCampaignFollowerById(campaignId)
                .map(AccountCampaignFollower::isReceiveNotifications)
                .orElse(false);
    }

    /**
     * toggle campaign notification status, returns true if success, false if not
     * following
     * 
     * @param id
     * @return
     */
    public boolean toggleCampaignNotification(Long id) {
        Optional<AccountCampaignFollower> acfOptional = getAccountCampaignFollowerById(id);
        AccountCampaignFollower acf = acfOptional.isPresent() ? acfOptional.get() : null;

        if (acf == null) {
            System.out.println("| [Account::toggleCampaignNotification] Account does not follow campaign id: " + id);
            return false;
        }

        acf.setReceiveNotifications(!acf.isReceiveNotifications());
        return true;
    }

    @Override
    public String toString() {
        return "Account [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName
                + ", phoneNumber=" + phoneNumber + ", role=" + role.getRoleName() + "]";
    }
}
