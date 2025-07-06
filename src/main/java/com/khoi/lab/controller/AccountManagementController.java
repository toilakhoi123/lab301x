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
                System.out.println("| Log in failed (wrong credentials)!");

                // login failure
                ModelAndView mav = new ModelAndView("login");
                mav.addObject("loginFailure", true);
                return mav;
            } else {
                // check if account is disabled
                if (!account.isDisabled()) {
                    System.out.println("| Successfully logged in!");
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
            System.err.println("| Already logged in!");
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

    // @GetMapping("/accounts")
    // public ModelAndView accountDashboard() {
    // return new String("admin/account_dashboard");
    // }
}
