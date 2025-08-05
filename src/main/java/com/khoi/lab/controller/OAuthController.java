package com.khoi.lab.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.DonationDAO;
import com.khoi.lab.entity.Account;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/oauth2")
public class OAuthController {
    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.secret}")
    private String googleClientSecret;
    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${facebook.client.id}")
    private String facebookClientId;
    @Value("${facebook.client.secret}")
    private String facebookClientSecret;
    @Value("${facebook.redirect.uri}")
    private String facebookRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AccountDAO accountDAO;
    private final DonationDAO donationDAO;

    /**
     * Deps injection constructor
     */
    public OAuthController(AccountDAO accountDAO, DonationDAO donationDAO) {
        this.accountDAO = accountDAO;
        this.donationDAO = donationDAO;
    }

    /**
     * Google login endpoint
     */
    @GetMapping("/login/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20profile%20email";
        response.sendRedirect(url);
    }

    /**
     * Google login callback
     * 
     * @param code
     * @return
     */
    @SuppressWarnings({ "rawtypes", "null", "unchecked" })
    @GetMapping("/callback/google")
    public ModelAndView handleGoogleCallback(@RequestParam String code, HttpSession session) {
        // Step 1: Exchange code for token
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("code", code);
        tokenRequest.put("client_id", googleClientId);
        tokenRequest.put("client_secret", googleClientSecret);
        tokenRequest.put("redirect_uri", googleRedirectUri);
        tokenRequest.put("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", tokenRequest, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Step 2: Fetch user info
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo", HttpMethod.GET, entity, Map.class);

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        System.out.println("Google login details: " + email + " | " + name);

        // Try to find the account by email
        Account existingAccount = accountDAO.accountFindWithEmail(email);
        if (existingAccount != null) {
            // === LOGIN EXISTING USER ===
            if (!existingAccount.isDisabled()) {
                session.setAttribute("account", existingAccount);
                ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
                mav.addObject("loginSuccess", true);
                return mav;
            } else {
                ModelAndView mav = new ModelAndView("login"); // adjust path as needed
                mav.addObject("loginDisabled", true);
                return mav;
            }
        }

        // === REGISTER NEW USER ===
        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "Google";
        String lastName = nameParts.length > 1 ? nameParts[1] : "User";

        String username = email.split("@")[0]; // use email prefix
        String phoneNumber = null; // leave empty or use fake number
        String password = ""; // empty password

        // Save to DB
        accountDAO.accountRegister(username, firstName, lastName, email, phoneNumber, password);

        // Fetch the new account and log in
        Account newAccount = accountDAO.accountFindWithEmail(email);
        session.setAttribute("account", newAccount);

        ModelAndView mav = (new GeneralController(donationDAO, accountDAO)).index();
        mav.addObject("registerSuccess", true);
        return mav;
    }

    /**
     * Facebook login endpoint
     */
    @GetMapping("/login/facebook")
    public void redirectToFacebook(HttpServletResponse response) throws IOException {
        String url = "https://www.facebook.com/v18.0/dialog/oauth"
                + "?client_id=" + facebookClientId
                + "&redirect_uri=" + facebookRedirectUri
                + "&response_type=code"
                + "&scope=email";
        response.sendRedirect(url);
    }

    /**
     * Facebook login callback
     */
    @SuppressWarnings({ "rawtypes", "null" })
    @GetMapping("/callback/facebook")
    public String handleFacebookCallback(@RequestParam("code") String code) {
        String tokenUrl = String.format(
                "https://graph.facebook.com/v18.0/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                facebookClientId, facebookRedirectUri, facebookClientSecret, code);

        Map token = restTemplate.getForObject(tokenUrl, Map.class);
        String accessToken = (String) token.get("access_token");

        String userInfoUrl = String.format(
                "https://graph.facebook.com/me?fields=id,name,email&access_token=%s",
                accessToken);

        Map userInfo = restTemplate.getForObject(userInfoUrl, Map.class);

        return userInfo.toString();
    }
}