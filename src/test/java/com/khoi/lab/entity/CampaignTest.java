package com.khoi.lab.entity;

import com.khoi.lab.enums.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Campaign entity.
 * This class uses Mockito to isolate the Campaign entity from its dependencies
 * (DonationReceiver and Donation)
 * and tests its logic thoroughly.
 */
@ExtendWith(MockitoExtension.class)
public class CampaignTest {
    @Mock
    private DonationReceiver mockReceiver;

    @Mock
    private Donation mockDonation1;
    @Mock
    private Donation mockDonation2;
    @Mock
    private Donation mockDonation3;

    @InjectMocks
    private Campaign campaign;

    private static final String CAMPAIGN_NAME = "Test Campaign";
    private static final String CAMPAIGN_DESCRIPTION = "A description for the test campaign.";
    private static final String VALID_IMAGE_URL = "http://example.com/image.jpg";
    private static final int GOAL = 10000;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now();
        endTime = startTime.plusDays(30);

        // Initialize a new Campaign instance before each test
        campaign = new Campaign(CAMPAIGN_NAME, mockReceiver, CAMPAIGN_DESCRIPTION, VALID_IMAGE_URL, GOAL, startTime,
                endTime);
    }

    @Test
    void testConstructorWithValidData() {
        // Verify that the constructor sets all initial properties correctly
        assertEquals(CAMPAIGN_NAME, campaign.getName(), "Campaign name should be set correctly.");
        assertEquals(mockReceiver, campaign.getReceiver(), "Receiver should be set correctly.");
        assertEquals(CAMPAIGN_DESCRIPTION, campaign.getDescription(), "Description should be set correctly.");
        assertEquals(VALID_IMAGE_URL, campaign.getImageUrl(), "Image URL should be set correctly.");
        assertEquals(GOAL, campaign.getGoal(), "Goal should be set correctly.");
        assertEquals(startTime, campaign.getStartTime(), "Start time should be set correctly.");
        assertEquals(endTime, campaign.getEndTime(), "End time should be set correctly.");
        assertEquals(CampaignStatus.CREATED, campaign.getStatus(), "Initial status should be CREATED.");
        assertNotNull(campaign.getDonations(), "Donations list should be initialized and not null.");
        assertTrue(campaign.getDonations().isEmpty(), "Donations list should be empty on creation.");
        assertNotNull(campaign.getFollowers(), "Followers list should be initialized and not null.");
        assertTrue(campaign.getFollowers().isEmpty(), "Followers list should be empty on creation.");
    }

    @Test
    void testConstructorWithNullOrEmptyImageUrl() {
        // Test that a null image URL uses the default value
        Campaign campaignWithNullImage = new Campaign(CAMPAIGN_NAME, mockReceiver, CAMPAIGN_DESCRIPTION, null, GOAL,
                startTime, endTime);
        assertEquals(
                "https://www.shutterstock.com/image-vector/fundraising-giving-heart-symbol-money-600nw-2509445751.jpg",
                campaignWithNullImage.getImageUrl());

        // Test that a blank image URL uses the default value
        Campaign campaignWithBlankImage = new Campaign(CAMPAIGN_NAME, mockReceiver, CAMPAIGN_DESCRIPTION, "   ", GOAL,
                startTime, endTime);
        assertEquals(
                "https://www.shutterstock.com/image-vector/fundraising-giving-heart-symbol-money-600nw-2509445751.jpg",
                campaignWithBlankImage.getImageUrl());
    }

    @Test
    void testSetters() {
        // Test various setters to ensure they update the state correctly
        String newName = "New Name";
        campaign.setName(newName);
        assertEquals(newName, campaign.getName());

        campaign.setStatus(CampaignStatus.OPEN);
        assertEquals(CampaignStatus.OPEN, campaign.getStatus());

        int newGoal = 20000;
        campaign.setGoal(newGoal);
        assertEquals(newGoal, campaign.getGoal());
    }

    @Test
    void testGetDonatedAmount_ConfirmedDonationsOnly() {
        when(mockDonation1.isConfirmed()).thenReturn(true);
        when(mockDonation1.getAmount()).thenReturn(500);

        when(mockDonation2.isConfirmed()).thenReturn(false); // This donation should be ignored

        when(mockDonation3.isConfirmed()).thenReturn(true);
        when(mockDonation3.getAmount()).thenReturn(1500);

        campaign.setDonations(Arrays.asList(mockDonation1, mockDonation2, mockDonation3));
        assertEquals(2000, campaign.getDonatedAmount(), "Donated amount should only sum confirmed donations.");
    }

    @Test
    void testGetDonatedPercentage_Below100Percent() {
        // Set up the state for a percentage calculation below 100%
        when(mockDonation1.isConfirmed()).thenReturn(true);
        when(mockDonation1.getAmount()).thenReturn(2000); // 20% of 10000 goal
        campaign.setDonations(Arrays.asList(mockDonation1));

        // The calculated percentage should be exactly 20.0
        assertEquals(20.0, campaign.getDonatedPercentage(), 0.01, "Donated percentage should be calculated correctly.");
    }

    @Test
    void testGetDonatedPercentage_Exceeds100Percent() {
        // Set up the state for a percentage calculation that exceeds 100%
        when(mockDonation1.isConfirmed()).thenReturn(true);
        when(mockDonation1.getAmount()).thenReturn(12000); // 120% of 10000 goal
        campaign.setDonations(Arrays.asList(mockDonation1));

        // The percentage should be capped at 100.0
        assertEquals(100.0, campaign.getDonatedPercentage(), 0.01,
                "Percentage should be capped at 100% when goal is exceeded.");
    }

    @Test
    void testGetDonatedPercentage_ZeroGoal() {
        // Test the edge case where the goal is zero or negative
        campaign.setGoal(0);
        when(mockDonation1.isConfirmed()).thenReturn(true);
        when(mockDonation1.getAmount()).thenReturn(1000);
        campaign.setDonations(Arrays.asList(mockDonation1));

        // The percentage should be 100.0
        assertEquals(100.0, campaign.getDonatedPercentage(), 0.01, "Percentage should be 100% if the goal is zero.");
    }

    @Test
    void testGetDaysLeft_FutureDate() {
        // Test calculation for a campaign that ends in the future
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10).plusHours(12);
        campaign.setEndTime(futureDate);
        // The days left should be -10 (rounding down)
        assertEquals(-10, campaign.getDaysLeft(), "Days left should be correct for a future end date.");
    }

    @Test
    void testGetDaysLeft_PastDate() {
        // Test calculation for a campaign that has already ended
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        campaign.setEndTime(pastDate);
        // Days left should be a negative number
        assertEquals(5, campaign.getDaysLeft(), "Days left should be negative for a past end date.");
    }

    @Test
    void testGetDaysLeft_NullEndTime() {
        // Test the edge case where the end time is null
        campaign.setEndTime(null);
        assertEquals(-1, campaign.getDaysLeft(), "Days left should be -1 if end time is null.");
    }

    @Test
    void testGetDonations_SortedAndFiltered() {
        // Prepare mock donations with different donate times and statuses
        when(mockDonation1.getDonateTime()).thenReturn(LocalDateTime.now().minusDays(1));
        when(mockDonation1.isRefused()).thenReturn(false);

        when(mockDonation2.getDonateTime()).thenReturn(LocalDateTime.now().minusDays(3));
        when(mockDonation2.isRefused()).thenReturn(true); // This donation should be filtered out

        when(mockDonation3.getDonateTime()).thenReturn(LocalDateTime.now().minusHours(1));
        when(mockDonation3.isRefused()).thenReturn(false);

        List<Donation> unsortedDonations = Arrays.asList(mockDonation1, mockDonation2, mockDonation3);
        campaign.setDonations(unsortedDonations);

        // Get the sorted and filtered list
        List<Donation> sortedAndFiltered = campaign.getDonations();

        // Verify the size of the returned list
        assertEquals(2, sortedAndFiltered.size(), "The list should only contain non-refused donations.");

        // Verify the sorting order (newest first)
        assertEquals(mockDonation3, sortedAndFiltered.get(0), "The newest donation should be first.");
        assertEquals(mockDonation1, sortedAndFiltered.get(1), "The second newest donation should be second.");
    }
}
