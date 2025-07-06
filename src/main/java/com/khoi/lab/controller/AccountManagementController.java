package com.khoi.lab.controller;

import org.springframework.stereotype.Controller;

import com.khoi.lab.dao.AccountDAO;

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
}
