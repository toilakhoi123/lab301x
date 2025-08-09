package com.khoi.lab;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import com.khoi.lab.service.UserPermissionService;

@ControllerAdvice
public class GlobalModelAttributes {
    @Autowired
    private UserPermissionService userPermissionService;

    @ModelAttribute("userPermissionService")
    public UserPermissionService getUserPermissionService() {
        return userPermissionService;
    }
}
