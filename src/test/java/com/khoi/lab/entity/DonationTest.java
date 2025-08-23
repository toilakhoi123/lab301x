package com.khoi.lab.entity;

import com.khoi.lab.enums.DonationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Donation entity.
 * This class uses Mockito to isolate the Donation entity from its dependencies
 * (Account and Campaign)
 * and tests its internal logic.
 */
@ExtendWith(MockitoExtension.class)
public class DonationTest {
    @Mock
    private Account mockAccount;

    @Mock
    private Campaign mockCampaign;

    @InjectMocks
    private Donation donation;

    private static final int DONATION_AMOUNT = 50000;
    private LocalDateTime donationTime;

    @BeforeEach
    void setUp() {
        donationTime = LocalDateTime.now();
        // Initialize a new Donation instance before each test
        donation = new Donation(mockAccount, mockCampaign, DONATION_AMOUNT, donationTime);
    }

    @Test
    void testConstructorWithValidData() {
        // Verify that the constructor correctly sets the initial properties
        assertEquals(mockAccount, donation.getAccount(), "Account should be set correctly.");
        assertEquals(mockCampaign, donation.getCampaign(), "Campaign should be set correctly.");
        assertEquals(DONATION_AMOUNT, donation.getAmount(), "Amount should be set correctly.");
        assertEquals(donationTime, donation.getDonateTime(), "Donate time should be set correctly.");
        assertEquals(DonationStatus.PENDING, donation.getStatus(), "Initial status should be PENDING.");
    }

    @Test
    void testConstructorWithNullAccount_AnonymousDonation() {
        // Test the constructor with a null account to simulate an anonymous donation
        Donation anonymousDonation = new Donation(null, mockCampaign, DONATION_AMOUNT, donationTime);
        assertNull(anonymousDonation.getAccount(), "Account should be null for an anonymous donation.");
        assertTrue(anonymousDonation.isAnonymous(), "isAnonymous() should return true for a null account.");
    }

    @Test
    void testSetters() {
        // Test various setters to ensure they update the state correctly
        long newId = 123L;
        donation.setId(newId);
        assertEquals(newId, donation.getId());

        int newAmount = 100000;
        donation.setAmount(newAmount);
        assertEquals(newAmount, donation.getAmount());

        donation.setStatus(DonationStatus.CONFIRMED);
        assertEquals(DonationStatus.CONFIRMED, donation.getStatus());
    }

    @Test
    void testIsAnonymous_WithAccount() {
        // The donation was created with a mocked account, so it should not be anonymous
        assertFalse(donation.isAnonymous(), "isAnonymous() should be false when an account is present.");
    }

    @Test
    void testIsAnonymous_WithoutAccount() {
        // Create a new donation with a null account
        Donation anonymousDonation = new Donation(null, mockCampaign, DONATION_AMOUNT, donationTime);
        assertTrue(anonymousDonation.isAnonymous(), "isAnonymous() should be true when the account is null.");
    }

    @Test
    void testIsPending_IsConfirmed_IsRefused() {
        // Initially, the status is PENDING
        assertTrue(donation.isPending(), "Initial status should be PENDING.");
        assertFalse(donation.isConfirmed(), "Donation should not be confirmed yet.");
        assertFalse(donation.isRefused(), "Donation should not be refused yet.");

        // Set status to CONFIRMED
        donation.setStatus(DonationStatus.CONFIRMED);
        assertFalse(donation.isPending(), "Status should no longer be PENDING.");
        assertTrue(donation.isConfirmed(), "Status should be CONFIRMED.");
        assertFalse(donation.isRefused(), "Status should not be REFUSED.");

        // Set status to REFUSED
        donation.setStatus(DonationStatus.REFUSED);
        assertFalse(donation.isPending(), "Status should no longer be PENDING.");
        assertFalse(donation.isConfirmed(), "Status should no longer be CONFIRMED.");
        assertTrue(donation.isRefused(), "Status should be REFUSED.");
    }

    @Test
    void testGetTimeAgo_JustNow() {
        // Test a time difference of less than 1 minute
        LocalDateTime time = LocalDateTime.now().minusSeconds(30);
        assertEquals("just now", donation.getTimeAgo(time), "Should return 'just now' for less than a minute.");
    }

    @Test
    void testGetTimeAgo_MinutesAgo() {
        // Test a time difference in minutes
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        assertEquals("10 minute(s) ago", donation.getTimeAgo(time), "Should return correct minutes ago string.");
    }

    @Test
    void testGetTimeAgo_HoursAgo() {
        // Test a time difference in hours
        LocalDateTime time = LocalDateTime.now().minusHours(5);
        assertEquals("5 hour(s) ago", donation.getTimeAgo(time), "Should return correct hours ago string.");
    }

    @Test
    void testGetTimeAgo_DaysAgo() {
        // Test a time difference in days
        LocalDateTime time = LocalDateTime.now().minusDays(3);
        assertEquals("3 day(s) ago", donation.getTimeAgo(time), "Should return correct days ago string.");
    }

    @Test
    void testGetTimeAgo_MoreThanAWeek() {
        // Test a time difference of 7 days or more, which should return a formatted
        // date
        LocalDateTime time = LocalDateTime.now().minusDays(8);
        String expectedFormat = time.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        assertEquals(expectedFormat, donation.getTimeAgo(time), "Should return formatted date for more than 7 days.");
    }
}
