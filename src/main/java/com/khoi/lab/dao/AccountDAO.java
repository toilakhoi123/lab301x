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
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param password
     * @return
     */
    Account accountRegister(String username, String firstName, String lastName, String email, String phoneNumber,
            String password);

    /**
     * Log an user in with their username/email/phone and password
     * 
     * @param usernameOrEmailOrPhone
     * @param password
     * @return
     */
    Account accountLogin(String usernameOrEmailOrPhone, String password);

    /**
     * Change current password to the new one
     * 
     * @param id
     * @param password
     * @param newPassword
     * @param newPasswordConfirm
     * @return
     */
    Account accountChangePassword(Long id, String password, String newPassword, String newPasswordConfirm);

    /**
     * Reset current password (forgotten) to the new one
     * 
     * @param id
     * @param newPassword
     * @param newPasswordConfirm
     * @return
     */
    Account accountResetPassword(Long id, String newPassword, String newPasswordConfirm);

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
