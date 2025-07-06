package com.khoi.lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.khoi.lab.dao.AccountDAO;

@Controller
public class GeneralController {
    private final AccountDAO accountDAO;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public GeneralController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Login page
     * 
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
