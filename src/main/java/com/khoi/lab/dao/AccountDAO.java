package com.khoi.lab.dao;

import java.util.List;

import com.khoi.lab.entity.Account;

/**
 * Data Access Object for Accounts
 */
public interface AccountDAO {
    /**
     * Initiate test data
     */
    void initiate();

    /**
     * Register new user account
     * 
     * @param username
     * @param name
     * @param email
     * @param phoneNumber
     * @param password
     * @return
     */
    Account accountRegister(String username, String name, String email, String phoneNumber, String password);

    /**
     * Log an user in with their username/email/phone and password
     * 
     * @param usernameOrEmailOrPhone
     * @param password
     * @return
     */
    Account accountLogin(String usernameOrEmailOrPhone, String password);

    void accountSave(Account account);

    Account accountUpdate(Account account);

    Account accountFindWithId(Long id);

    Account accountFindWithUsername(String username);

    /**
     * Get full list of existing accounts
     * 
     * @return
     */
    List<Account> accountList();
}
