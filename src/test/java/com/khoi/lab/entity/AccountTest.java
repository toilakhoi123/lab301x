package com.khoi.lab.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.khoi.lab.enums.UserPermission;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountTest {
    @Mock
    private Role mockRole;

    @Mock
    private Campaign mockCampaign1;

    @Mock
    private Campaign mockCampaign2;

    @InjectMocks
    private Account account;

    @BeforeEach
    void setUp() {
        // We'll initialize the account here, but move the mock setup
        // to the specific test methods that need it.
        account = new Account("testuser", "John", "Doe", "john.doe@test.com", "0123456789", "password123", mockRole);
    }

    @Test
    void testAccountCreation() {
        // Test basic constructor and getter methods
        assertEquals("testuser", account.getUsername());
        assertEquals("John", account.getFirstName());
        assertEquals("Doe", account.getLastName());
        assertEquals("John Doe", account.getFullName());
        assertEquals("john.doe@test.com", account.getEmail());
        assertEquals("0123456789", account.getPhoneNumber());
        assertFalse(account.isDisabled());
    }

    @Test
    void testHasPermission() {
        // This test requires the mockRole to have permissions, so we set up the
        // stubbing here.
        when(mockRole.getPermissions())
                .thenReturn(Arrays.asList(UserPermission.CREATE_DONATIONS, UserPermission.CREATE_COMMENTS));

        // Test the hasPermission helper method
        assertTrue(account.hasPermission(UserPermission.CREATE_DONATIONS));
        assertTrue(account.hasPermission(UserPermission.CREATE_COMMENTS));
        assertFalse(account.hasPermission(UserPermission.VIEW_DASHBOARD));
    }

    @Test
    void testAddAndRemoveFollowedCampaign() {
        // Set up mock behavior for campaigns used in this test
        when(mockCampaign1.getId()).thenReturn(1L);

        // Test adding a followed campaign
        account.addFollowedCampaign(mockCampaign1, true);
        assertEquals(1, account.getFollowedCampaigns().size());
        assertTrue(account.isFollowingCampaignId(1L));

        // Test removing a followed campaign
        account.removeFollowedCampaign(mockCampaign1);
        assertEquals(0, account.getFollowedCampaigns().size());
        assertFalse(account.isFollowingCampaignId(1L));
    }

    @Test
    void testToggleCampaignNotification() {
        // Set up mock behavior for campaigns used in this test
        when(mockCampaign1.getId()).thenReturn(1L);

        // Add a campaign to follow first
        account.addFollowedCampaign(mockCampaign1, true);
        assertTrue(account.isCampaignIdEnableNotifications(1L));

        // Toggle notifications off
        assertTrue(account.toggleCampaignNotification(1L));
        assertFalse(account.isCampaignIdEnableNotifications(1L));

        // Toggle notifications on
        assertTrue(account.toggleCampaignNotification(1L));
        assertTrue(account.isCampaignIdEnableNotifications(1L));

        // Test with a non-followed campaign
        assertFalse(account.toggleCampaignNotification(2L));
    }

    @Test
    void testGetAccountCampaignFollowerById_found() {
        // Set up mock behavior for a campaign
        when(mockCampaign1.getId()).thenReturn(1L);

        // Add a campaign to follow
        account.addFollowedCampaign(mockCampaign1, true);

        // Get the follower and verify it's present
        Optional<AccountCampaignFollower> follower = account.getAccountCampaignFollowerById(1L);
        assertTrue(follower.isPresent());
        assertEquals(1L, follower.get().getCampaign().getId());
    }

    @Test
    void testGetAccountCampaignFollowerById_notFound() {
        // Test with a campaign ID that hasn't been added
        Optional<AccountCampaignFollower> follower = account.getAccountCampaignFollowerById(99L);
        assertFalse(follower.isPresent());
    }
}
