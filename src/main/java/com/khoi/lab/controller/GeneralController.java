package com.khoi.lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GeneralController {
    /**
     * DAO Initiator
     * 
     * @param accountDAO
     */
    public GeneralController() {
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
