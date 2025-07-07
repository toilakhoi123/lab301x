package com.khoi.lab.controller;

import org.springframework.stereotype.Controller;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.entity.Account;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AccountDAO accountDAO;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public AdminController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Manage accounts page
     * (notLoggedIn/notAuthorized)
     * 
     * @param session
     * @return
     */
    @GetMapping("/accounts")
    public ModelAndView accountsManage(
            HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notLoggedIn", true);
            return mav;
        } else if (!account.isAdmin()) {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("notAuthorized", true);
            return mav;
        }

        ModelAndView mav = new ModelAndView("admin/accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }

    /**
     * Handle account edit request
     * 
     * @param id
     * @param isDisabled
     * @param isAdmin
     * @return
     */
    @PostMapping("/accounts/edit")
    public ModelAndView accountsEditRequest(
            @RequestParam Long id,
            @RequestParam(required = false) String isDisabled,
            @RequestParam(required = false) String isAdmin) {
        Account account = accountDAO.accountFindWithId(id);

        System.out.println("isDisabled: " + isDisabled);
        System.out.println("isAdmin: " + isAdmin);

        if (isDisabled != null) {
            System.out.println("| Edited " + account + ", isDisabled = " + isDisabled);
            account.setDisabled(isDisabled.equals("true"));
            accountDAO.accountUpdate(account);
        }

        if (isAdmin != null) {
            System.out.println("| Edited " + account + ", isAdmin = " + isAdmin);
            account.setDisabled(isAdmin.equals("true"));
            accountDAO.accountUpdate(account);
        }

        ModelAndView mav = new ModelAndView("admin/accounts");
        mav.addObject("accounts", accountDAO.accountList());
        return mav;
    }
}
