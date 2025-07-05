package com.khoi.lab.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.khoi.lab.dao.AccountDAO;

@Controller
public class HelloController {
    @Value("${spring.application.name}")
    String appName;

    private final AccountDAO accountDAO;

    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public HelloController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Home page
     * 
     * @param model
     * @return
     */
    @GetMapping("/")
    public String homePage(Model model) {
        System.out.println("Displaying home page.");
        model.addAttribute("appName", appName);
        return "home";
    }
}
