package com.khoi.lab.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.entity.Donation;
import com.khoi.lab.entity.DonationReceiver;
import com.khoi.lab.enums.CampaignStatus;
import com.khoi.lab.service.CampaignStatusUpdaterService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * DAO for Donations
 */
@Repository
public class DonationDAOImpl implements DonationDAO {
    private EntityManager em;

    public DonationDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void initiate() {
        // donation receivers
        DonationReceiver dr1 = donationReceiverCreate("Chang Thi Ha", "09182818992");
        DonationReceiver dr2 = donationReceiverCreate("Minh Dang", "07919192099");
        DonationReceiver dr3 = donationReceiverCreate("Quy Tu tam Dak Lak", "01129439122");

        // campaigns
        String campaign1desc = "Bé Cháng Thị Hà sinh năm 2012, là con của một gia đình dân tộc H’Mông nghèo khó tại thôn Ea Uôl, xã Cư Pui, huyện Krông Bông, tỉnh Đắk Lắk. Cuộc sống vốn đã khó khăn, lại càng khốn đốn khi bé Hà phát sinh dấu hiệu bất thường ở vùng cổ. Kết quả siêu âm cho thấy bé có tổn thương ở góc hàm trái và hạch cổ hai bên. Khối u ngày càng to dần, gây ảnh hưởng nghiêm trọng đến sức khỏe của bé.";
        Campaign campaign1 = campaignCreate("Xin giúp bé Cháng Thị Hà chữa bệnh hiểm nghèo", dr1, campaign1desc,
                30000000, LocalDateTime.of(2025, 7, 10, 0, 0), LocalDateTime.of(2025, 7, 30, 0, 0));

        String campaign2desc = "Xin Quý Ân Nhân hãy cùng góp sức, để bé Minh Đăng có thêm cơ hội được sống, để nụ cười thơ ngây ấy tiếp tục được nở, và những ước mơ nhỏ bé còn dang dở được viết tiếp.";
        Campaign campaign2 = campaignCreate("Xin giữ lấy sợi dây sinh mệnh cho bé Minh Đăng", dr2, campaign2desc,
                20000000, LocalDateTime.of(2025, 7, 15, 0, 0), LocalDateTime.of(2025, 7, 25, 0, 0));

        String campaign3desc = "Bé Y Sáng Buôn Dap (1 tuổi, dân tộc M’nông) bị bệnh tim bẩm sinh. Nhờ sự hỗ trợ của các tổ chức, chi phí phẫu thuật tim sẽ được tài trợ – điều mà gia đình bé Y Sáng không dám mơ tới.";
        Campaign campaign3 = campaignCreate("Giúp bố mẹ nghèo có thêm hy vọng cứu con", dr3, campaign3desc,
                20000000, LocalDateTime.of(2025, 7, 10, 0, 0), LocalDateTime.of(2025, 7, 30, 0, 0));

        // donations
        Account account1 = em.find(Account.class, 1);
        Account account2 = em.find(Account.class, 2);
        Account account3 = em.find(Account.class, 3);

        (new CampaignStatusUpdaterService(this)).updateCampaignStatuses();

        accountDonate(campaign1, account1, 1500000);
        accountDonate(campaign1, account1, 500000);
        accountDonate(campaign2, account1, 25000000);
        accountDonate(campaign3, account1, 300000);
        accountDonate(campaign1, account2, 500000);
        accountDonate(campaign3, account2, 25000000);
        accountDonate(campaign1, account3, 3500000);
    }

    // TESTED
    @Override
    @Transactional
    public DonationReceiver donationReceiverCreate(String name, String phoneNumber) {
        DonationReceiver donationReceiver = new DonationReceiver(name, phoneNumber);
        return donationReceiverSave(donationReceiver);
    }

    // TESTED
    @Override
    @Transactional
    public DonationReceiver donationReceiverSave(DonationReceiver donationReceiver) {
        em.persist(donationReceiver);
        System.out.println("| [donationReceiverSave] Saved donation receiver: " + donationReceiver);
        return donationReceiver;
    }

    // TESTED
    @Override
    @Transactional
    public DonationReceiver donationReceiverUpdate(DonationReceiver donationReceiver) {
        donationReceiver = em.merge(donationReceiver);
        System.out.println("| [donationReceiverUpdate] Updated donation receiver: " + donationReceiver);
        return donationReceiver;
    }

    // TESTED
    @Override
    public DonationReceiver donationReceiverFindByPhoneNumber(String phoneNumber) {
        TypedQuery<DonationReceiver> tq = em.createQuery(
                "SELECT dr FROM DonationReceiver dr WHERE dr.phoneNumber=:phoneNumber",
                DonationReceiver.class);
        tq.setParameter("phoneNumber", phoneNumber);
        try {
            DonationReceiver donationReceiver = tq.getSingleResult();
            System.out.println("| [donationReceiverFindByPhoneNumber] Donation receiver found: " + donationReceiver);
            return donationReceiver;
        } catch (NoResultException e) {
            System.out.println("| [donationReceiverFindByPhoneNumber] Donation receiver not found!");
            return null;
        }
    }

    // TESTED
    @Override
    @Transactional
    public Campaign campaignCreate(String name, DonationReceiver donationReceiver, String description, int goal,
            LocalDateTime startTime, LocalDateTime endTime) {
        Campaign campaign = new Campaign(name, donationReceiver, description, goal, startTime, endTime);
        return campaignSave(campaign);
    }

    // TESTED
    @Override
    public Campaign campaignFindById(Long id) {
        Campaign campaign = em.find(Campaign.class, id);
        if (campaign == null) {
            System.out.println("| [campaignFindById] Couldn't find campaign with id: " + id);
        } else {
            System.out.println("| [campaignFindById] Found campaign: " + campaign);
        }
        return campaign;
    }

    // TESTED
    @Override
    @Transactional
    public Campaign campaignAddTimeMinutes(Campaign campaign, Long minutes) {
        LocalDateTime endTimeOld = campaign.getEndTime();
        LocalDateTime endtimeNew = endTimeOld.plusMinutes(minutes);
        campaign.setEndTime(endtimeNew);
        System.out.println("| [campaignAddTimeMinutes] End time modified from " + endTimeOld + " -> " + endtimeNew);
        return campaignSave(campaign);
    }

    // TESTED
    @Override
    public int campaignGetDonatedAmount(Campaign campaign) {
        int amount = campaign.getDonatedAmount();
        System.out.println("| [campaignGetDonatedAmount] Total donated amount: " + amount);
        return amount;
    }

    // TESTED
    @Override
    public double campaignGetDonatedPercentage(Campaign campaign) {
        int amount = campaignGetDonatedAmount(campaign);
        int goal = campaign.getGoal();

        if (goal == 0) {
            return 100;
        }

        BigDecimal amountBD = BigDecimal.valueOf(amount);
        BigDecimal goalBD = BigDecimal.valueOf(goal);
        BigDecimal percentage = amountBD
                .divide(goalBD, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);

        System.out.println(
                "| [campaignGetDonatedPercentage] Total donated percentage: " + percentage.doubleValue() + "%");
        return percentage.doubleValue();
    }

    // TESTED
    @Override
    public List<Donation> campaignGetDonations(Campaign campaign, boolean excludeConfirmed) {
        List<Donation> donations = excludeConfirmed
                ? campaign.getDonations().stream().filter(d -> !d.isConfirmed()).toList()
                : campaign.getDonations();
        System.out.println("| [campaignGetDonations] Found and returned: " + donations.size() + " donations");
        return donations;
    }

    // TESTED
    @Override
    public HashMap<Account, Integer> campaignGetDonatorsAndDonatedAmount(Campaign campaign) {
        HashMap<Account, Integer> map = new HashMap<>();

        for (Donation donation : campaign.getDonations()) {
            if (!donation.isConfirmed())
                continue;

            Account account = donation.getAccount();
            Integer donatedAmount = donation.getAmount();

            if (map.containsKey(account)) {
                Integer amountOld = map.get(account);
                map.put(account, amountOld + donatedAmount);
            } else {
                map.put(account, donatedAmount);
            }
        }

        System.out.println(
                "| [campaignGetDonatorsAndDonatedAmount] Found and returned: " + map.size()
                        + " people and their donations");

        return map;
    }

    // TESTED
    @Override
    @Transactional
    public Campaign campaignChangeStatus(Campaign campaign, CampaignStatus status) {
        campaign.setStatus(status);
        campaign = campaignUpdate(campaign);
        System.out.println("| [campaignChangeStatus] Modified campaign status to: " + campaign);
        return campaign;
    }

    // TESTED
    @Override
    public List<Campaign> campaignFindByStatus(CampaignStatus status) {
        TypedQuery<Campaign> tq = em.createQuery(
                "SELECT c FROM Campaign c WHERE c.status=:status",
                Campaign.class);
        tq.setParameter("status", status);

        List<Campaign> campaigns = tq.getResultList();
        System.out.println("| [campaignFindByStatus] Found and returned: " + campaigns.size()
                + " campaigns with status: " + status.name());

        return campaigns;
    }

    // TESTED
    @Override
    public Campaign campaignFindByDonationReceiverPhoneNumber(String phoneNumber) {
        DonationReceiver donationReceiver = donationReceiverFindByPhoneNumber(phoneNumber);

        // find campaign
        TypedQuery<Campaign> tq = em.createQuery(
                "SELECT c FROM Campaign c WHERE c.receiver=:receiver",
                Campaign.class);
        tq.setParameter("receiver", donationReceiver);
        try {
            Campaign campaign = tq.getSingleResult();
            System.out.println(
                    "| [campaignFindByDonationReceiverPhoneNumber] Campaign found: " + campaign);
            return campaign;
        } catch (NoResultException e) {
            System.out.println("| [campaignFindByDonationReceiverPhoneNumber] Campaign not found!");
            return null;
        }
    }

    // TESTED
    @Override
    @Transactional
    public Campaign campaignSave(Campaign campaign) {
        em.persist(campaign);
        System.out.println("| [campaignSave] Saved campaign: " + campaign);
        return campaign;
    }

    // TESTED
    @Override
    @Transactional
    public Campaign campaignUpdate(Campaign campaign) {
        campaign = em.merge(campaign);
        System.out.println("| [campaignUpdate] Updated campaign: " + campaign);
        return campaign;
    }

    // TESTED
    @Override
    public List<Campaign> campaignList() {
        TypedQuery<Campaign> tq = em.createQuery(
                "SELECT c FROM Campaign c",
                Campaign.class);
        List<Campaign> campaigns = tq.getResultList();
        System.out.println("| [campaignList] Found and returned: " + campaigns.size() + " campaigns!");
        return campaigns;
    }

    // TESTED
    @Override
    public List<Donation> accountGetDonations(Account account) {
        List<Donation> donations = account.getDonations();
        System.out.println("| [accountGetDonations] Found and returned: " + donations.size() + " donations");
        return donations;
    }

    // TESTED
    @Override
    @Transactional
    public Donation accountDonate(Campaign campaign, Account account, int amount) {
        campaign = em.find(Campaign.class, campaign.getId());

        // check campaign status
        if (campaign.getStatus() != CampaignStatus.OPEN
                && campaign.getStatus() != CampaignStatus.COMPLETE) {
            System.out.println("| [accountDonate] Unable to donate! Campaign status is: " + campaign.getStatus());
            return null;
        }

        // check if anonymous
        if (account != null) {
            account = em.find(Account.class, account.getId());
        }

        // create donation
        Donation donation = new Donation(account, campaign, amount, LocalDateTime.now());
        donationSave(donation);

        // update campaign & account
        campaign.getDonations().add(donation);
        campaignUpdate(campaign);
        if (account != null) {
            account.getDonations().add(donation);
            em.merge(account);
        }

        System.out.println("| [accountDonate] " + (account == null ? "Anonymous" : account.getFullName()) + " donated "
                + amount + " to " + campaign);

        return donation;
    }

    // TESTED
    @Override
    @Transactional
    public Donation donationConfirm(Donation donation) {
        donation.setConfirmed(true);
        donation = donationSave(donation);
        System.out.println("| [donationConfirm] Confirmed donation: " + donation);
        (new CampaignStatusUpdaterService(this)).updateCampaignStatuses();
        return donation;
    }

    // TESTED
    @Override
    @Transactional
    public Donation donationSave(Donation donation) {
        em.persist(donation);
        System.out.println("| [donationSave] Saved donation: " + donation);
        return donation;
    }

    // TESTED
    @Override
    @Transactional
    public Donation donationUpdate(Donation donation) {
        donation = em.merge(donation);
        System.out.println("| [donationUpdate] Updated donation: " + donation);
        return donation;
    }

    // TESTED
    @Override
    public List<Donation> donationList() {
        TypedQuery<Donation> tq = em.createQuery(
                "SELECT d FROM Donation d",
                Donation.class);
        List<Donation> donations = tq.getResultList();
        System.out.println("| [donationList] Found and returned: " + donations.size() + " donations!");
        return donations;
    }

    // TESTED
    @Override
    public Donation donationFindById(Long id) {
        Donation donation = em.find(Donation.class, id);
        if (donation == null) {
            System.out.println("| [donationFindById] Couldn't find donation with id: " + id);
        } else {
            System.out.println("| [donationFindById] Found donation: " + donation);
        }
        return donation;
    }

    // TESTED
    @Override
    @Transactional
    public void campaignDeleteById(Long id) {
        Campaign campaign = campaignFindById(id);
        em.remove(campaign);
        System.out.println("| [campaignDeleteById] Deleted campaign with id: " + id);
    }
}
