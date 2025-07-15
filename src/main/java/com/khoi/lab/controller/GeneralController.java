package com.khoi.lab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Campaign;
import com.khoi.lab.enums.CampaignStatus;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GeneralController {
    private final DonationDAO donationDAO;

    /**
     * DAO Initiator
     * 
     * @param donationDAO
     */
    public GeneralController(DonationDAO donationDAO) {
        this.donationDAO = donationDAO;
    }

    /**
     * Login page
     * 
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        List<Campaign> campaigns = donationDAO.campaignList().stream()
                .filter(c -> c.getDonatedPercentageUncapped() < 100)
                .sorted((a, b) -> Integer.compare(b.getDonatedPercentageUncapped(), a.getDonatedPercentageUncapped()))
                .limit(3)
                .toList();

        mav.addObject("campaigns", campaigns);
        return mav;
    }

    /**
     * View currently active campaigns
     * 
     * @param id
     * @return
     */
    @GetMapping("/campaigns")
    public ModelAndView campaignsPage() {
        List<Campaign> campaignsCreated = donationDAO.campaignFindByStatus(CampaignStatus.CREATED);
        List<Campaign> campaignsOpen = donationDAO.campaignFindByStatus(CampaignStatus.OPEN);
        List<Campaign> campaignsComplete = donationDAO.campaignFindByStatus(CampaignStatus.COMPLETE);
        List<Campaign> campaignsClosed = donationDAO.campaignFindByStatus(CampaignStatus.CLOSED);

        ModelAndView mav = new ModelAndView("campaigns");
        mav.addObject("campaignsCreated", campaignsCreated);
        mav.addObject("campaignsOpen", campaignsOpen);
        mav.addObject("campaignsComplete", campaignsComplete);
        mav.addObject("campaignsClosed", campaignsClosed);
        return mav;
    }

    /**
     * View a campaign's details
     * (campaignNotExist)
     * 
     * @param id
     * @return
     */
    @GetMapping("/campaigns/campaign")
    public ModelAndView campaignsViewDetail(@RequestParam Long id) {
        Campaign campaign = donationDAO.campaignFindById(id);

        if (campaign == null) {
            ModelAndView mav = campaignsPage();
            mav.addObject("campaignNotExist", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("campaign-details");
        mav.addObject("campaign", campaign);
        return mav;
    }
}
