package com.khoi.lab.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationPaymentCode;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.CampaignStatus;
import com.khoi.lab.enums.DonationStatus;
import com.khoi.lab.service.EmailSenderService;
import com.khoi.lab.service.UserPermissionService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ExtendWith(MockitoExtension.class)
public class DonationDAOImplTest {
    // Mocking dependencies
    @Mock
    private EntityManager em;
    @Mock
    private EmailSenderService senderService;
    @Mock
    private UserPermissionService userPermissionService;
    @Mock
    private TypedQuery<Campaign> mockCampaignTypedQuery;
    @Mock
    private TypedQuery<DonationReceiver> mockDrTypedQuery;
    @Mock
    private TypedQuery<Donation> mockDonationTypedQuery;
    @Mock
    private TypedQuery<DonationPaymentCode> mockPaymentCodeTypedQuery;

    // Class under test, with mocked dependencies injected
    @InjectMocks
    private DonationDAOImpl donationDAO;

    /**
     * Test for accountDonate()
     * Scenario: Failed donation due to wrong campaign status.
     * Verifies that the donation is not created.
     */
    @Test
    void accountDonate_InvalidCampaignStatus_ReturnsNull() {
        // Setup mock data
        Account account = new Account();
        account.setId(1L);
        Campaign campaign = new Campaign();
        campaign.setId(10L);
        campaign.setStatus(CampaignStatus.CLOSED);

        when(em.find(eq(Campaign.class), any())).thenReturn(campaign);

        // Call the method
        Donation result = donationDAO.accountDonate(campaign, account, 100000);

        // Assertions
        assertNull(result);

        // Verify no database operations were performed
        verify(em, never()).persist(any(Donation.class));
    }

    /**
     * Test for campaignChangeStatus()
     * Verifies that the campaign's status is correctly updated.
     */
    @Test
    void campaignChangeStatus_UpdatesStatus() {
        // Setup mock data
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.OPEN);

        // Mock the merge method to return the updated campaign
        when(em.merge(any(Campaign.class))).thenReturn(campaign);

        // Call the method
        Campaign result = donationDAO.campaignChangeStatus(campaign, CampaignStatus.CLOSED);

        // Assertions
        assertNotNull(result);
        assertEquals(CampaignStatus.CLOSED, result.getStatus());

        // Verify the merge method was called once
        verify(em).merge(campaign);
    }

    /**
     * Test for campaignFindByStatus()
     * Verifies that the method correctly queries for campaigns with a specific
     * status.
     */
    @Test
    void campaignFindByStatus_ReturnsCorrectCampaigns() {
        // Setup mock data
        Campaign campaign1 = new Campaign();
        campaign1.setStatus(CampaignStatus.OPEN);
        Campaign campaign2 = new Campaign();
        campaign2.setStatus(CampaignStatus.OPEN);

        List<Campaign> campaigns = Arrays.asList(campaign1, campaign2);

        // Mock the EntityManager and TypedQuery to return the predefined list
        when(em.createQuery(anyString(), eq(Campaign.class))).thenReturn(mockCampaignTypedQuery);
        when(mockCampaignTypedQuery.setParameter(anyString(), any(CampaignStatus.class)))
                .thenReturn(mockCampaignTypedQuery);
        when(mockCampaignTypedQuery.getResultList()).thenReturn(campaigns);

        // Call the method
        List<Campaign> result = donationDAO.campaignFindByStatus(CampaignStatus.OPEN);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getStatus() == CampaignStatus.OPEN));
    }

    /**
     * Test for donationGetTotalRecent()
     * Verifies that the method correctly sums confirmed donations within the time
     * range.
     * This test no longer uses a @Spy. It mocks the return value of the query
     * instead of mocking the internal donationList method.
     */
    @Test
    void donationGetTotalRecent_ReturnsCorrectTotal() {
        // Setup mock donations
        Donation d1 = new Donation();
        d1.setAmount(1000);
        d1.setStatus(DonationStatus.CONFIRMED);
        d1.setDonateTime(LocalDateTime.now().minusMinutes(5));

        Donation d2 = new Donation();
        d2.setAmount(2000);
        d2.setStatus(DonationStatus.CONFIRMED);
        d2.setDonateTime(LocalDateTime.now().minusMinutes(15));

        Donation d3 = new Donation();
        d3.setAmount(5000);
        d3.setStatus(DonationStatus.PENDING); // Should be ignored
        d3.setDonateTime(LocalDateTime.now().minusMinutes(2));

        Donation d4 = new Donation();
        d4.setAmount(4000);
        d4.setStatus(DonationStatus.CONFIRMED);
        d4.setDonateTime(LocalDateTime.now().minusMinutes(61)); // Outside the 60 min window

        List<Donation> allDonations = Arrays.asList(d1, d2, d3, d4);

        // Mock the EntityManager's createQuery and the TypedQuery's getResultList
        when(em.createQuery(anyString(), eq(Donation.class))).thenReturn(mockDonationTypedQuery);
        when(mockDonationTypedQuery.getResultList()).thenReturn(allDonations);

        // Call the method
        int total = donationDAO.donationGetTotalRecent(60L);

        // Assertions
        assertEquals(3000, total); // 1000 (d1) + 2000 (d2)
        // d4 is outside the 60 minute window and d3 is not confirmed.
    }
}
