package com.khoi.lab.controller;

import org.springframework.stereotype.Controller;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.entity.Account;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountManagementController {
    private final AccountDAO accountDAO;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public AccountManagementController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Login page
     * 
     * @return
     */
    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /**
     * Handles login request
     * (loginSuccess/loginFailure/loginAlready/loginDisabled)
     * 
     * @param session
     * @param usernameOrEmailOrPhone
     * @param password
     * @param rememberMe
     * @return
     */
    @PostMapping("/login")
    public ModelAndView loginRequest(
            HttpSession session,
            @RequestParam String usernameOrEmailOrPhone,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "false") boolean rememberMe) {
        // check if not logged in
        if ((Account) session.getAttribute("account") == null) {
            Account account = accountDAO.accountLogin(usernameOrEmailOrPhone, password);

            if (account == null) {
                // login failure
                ModelAndView mav = new ModelAndView("login");
                mav.addObject("loginFailure", true);
                return mav;
            } else {
                // check if account is disabled
                if (!account.isDisabled()) {
                    session.setAttribute("account", account);

                    // login success
                    ModelAndView mav = new ModelAndView("index");
                    mav.addObject("loginSuccess", true);
                    return mav;
                } else {
                    System.out.println("| Account is disabled by admins!!!");

                    // account disabled
                    ModelAndView mav = new ModelAndView("index");
                    mav.addObject("loginDisabled", true);
                    return mav;
                }
            }
        } else {
            System.out.println("| Already logged in!");
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("loginAlready", true);
            return mav;
        }
    }

    /**
     * Register page
     * 
     * @return
     */
    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    /**
     * Handles register request
     * (registerSuccess/registerPasswordMismatch/registerUsernameExists/registerUsernameExists/registerPhoneNumberExists)
     * 
     * @param firstName
     * @param lastName
     * @param username
     * @param phoneNumber
     * @param email
     * @param password
     * @param passwordConfirm
     * @return
     */
    @PostMapping("/register")
    public ModelAndView registerRequest(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String passwordConfirm) {
        System.out.println("registering..");

        // check password confirm
        if (!password.equals(passwordConfirm)) {
            System.out.println("| Password mismatch: " + password + " vs " + passwordConfirm);
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("registerPasswordMismatch", true);
            return mav;
        }

        // check account username exist
        if (accountDAO.accountFindWithUsername(username) != null) {
            System.out.println("| Username exists!");
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("registerUsernameExists", true);
            return mav;
        }

        // check account username exist
        if (accountDAO.accountFindWithEmail(email) != null) {
            System.out.println("| Email exists!");
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("registerEmailExists", true);
            return mav;
        }

        // check account phone number exist
        if (accountDAO.accountFindWithPhoneNumber(phoneNumber) != null) {
            System.out.println("| Phone number exists!");
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("registerPhoneNumberExists", true);
            return mav;
        }

        // register successful
        accountDAO.accountRegister(username, firstName, lastName, email, phoneNumber, password);
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("registerSuccess", true);
        return mav;
    }

    /**
     * Log out the current user
     * 
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("account");
        System.out.println("| Logged the current user out!");
        return new ModelAndView("index");
    }
}
