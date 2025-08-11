package com.khoi.lab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.entity.Account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Campaign status updater service
 */
@Service
public class AccountSessionUpdaterService implements HandlerInterceptor {
    @Autowired
    private AccountDAO accountDAO;

    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Account account = (Account) session.getAttribute("account");
            if (account != null) {
                Account updatedAccount = accountDAO.accountFindWithId(account.getId());
                session.setAttribute("account", updatedAccount);
            }
        }
        return true;
    }
}
