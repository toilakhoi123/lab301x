package com.khoi.lab.data;

import java.util.List;
import java.util.Map;

/**
 * Store email templates
 */
public class EmailTemplates {
    public static final Map<String, List<String>> EMAIL_TEMPLATES = Map.of(
            "CAMPAIGN_OPENED",
            List.of("Campaign Open For Donates!",
                    "**{CAMPAIGN_NAME}** is now open for donations! Donate now!\n{CAMPAIGN_LINK}"),
            "CAMPAIGN_COMPLETED",
            List.of("Campaign Completed Donation Quota!",
                    "Thanks to the generosity of all donators, **{CAMPAIGN_NAME}** has completed their donation goal of {CAMPAIGN_GOAL}! Thank you!\n{CAMPAIGN_LINK}"),
            "CAMPAIGN_CLOSED", List.of("Campaign Ended!",
                    "**{CAMPAIGN_NAME}** has ended! Thank you!\n{CAMPAIGN_LINK}"),
            "CAMPAIGN_REOPENED", List.of("Campaign Re-Opened!",
                    "**{CAMPAIGN_NAME}** has just been re-opened, and will end at {CAMPAIGN_END_TIME}! Donate now!\n{CAMPAIGN_LINK}"));

    public static List<String> getEmailTemplateForEvent(String eventName) {
        return EMAIL_TEMPLATES.getOrDefault(eventName, null);
    }
}
