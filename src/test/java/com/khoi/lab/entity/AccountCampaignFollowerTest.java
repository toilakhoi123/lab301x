package com.khoi.lab.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AccountCampaignFollower entity.
 */
@ExtendWith(MockitoExtension.class)
public class AccountCampaignFollowerTest {
    @Mock
    private Account mockAccount;

    @Mock
    private Campaign mockCampaign;

    @InjectMocks
    private AccountCampaignFollower follower;

    @BeforeEach
    void setUp() {
        // Initialize the follower with mock objects
        follower = new AccountCampaignFollower(mockAccount, mockCampaign, true);
    }

    @Test
    void testConstructorAndGetters() {
        // Verify the initial state set by the constructor
        assertEquals(mockAccount, follower.getAccount(),
                "Account should match the mock object passed to the constructor.");
        assertEquals(mockCampaign, follower.getCampaign(),
                "Campaign should match the mock object passed to the constructor.");
        assertTrue(follower.isReceiveNotifications(), "Notifications should be enabled by default for this test.");
    }

    @Test
    void testSetters() {
        // Create new mock objects for setters
        Account newMockAccount = new Account();
        Campaign newMockCampaign = new Campaign();

        // Test setters to ensure they update the state correctly
        follower.setAccount(newMockAccount);
        assertEquals(newMockAccount, follower.getAccount(), "setAccount should update the account object.");

        follower.setCampaign(newMockCampaign);
        assertEquals(newMockCampaign, follower.getCampaign(), "setCampaign should update the campaign object.");

        follower.setReceiveNotifications(false);
        assertFalse(follower.isReceiveNotifications(), "setReceiveNotifications should update the boolean value.");

        follower.setReceiveNotifications(true);
        assertTrue(follower.isReceiveNotifications(),
                "setReceiveNotifications should update the boolean value back to true.");
    }

    @Test
    void testIsReceiveNotifications() {
        // Test the isReceiveNotifications method
        assertTrue(follower.isReceiveNotifications(), "Notifications should be enabled initially.");

        follower.setReceiveNotifications(false);
        assertFalse(follower.isReceiveNotifications(), "Notifications should be disabled after being set to false.");
    }

    @Test
    void testSetId() {
        // Test setting the ID
        Long testId = 123L;
        follower.setId(testId);
        assertEquals(testId, follower.getId(), "The ID should be set correctly.");
    }
}
