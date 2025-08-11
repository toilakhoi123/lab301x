package com.khoi.lab.dao;

import java.util.List;

import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.PasswordResetCode;
import com.khoi.lab.entity.Role;
import com.khoi.lab.enums.UserPermission;

/**
 * Data Access Object for Accounts
 */
public interface AccountDAO {
    /**
     * Initiate test data
     */
    void initiate();

    /**
     * Create a role with desired permissions
     * 
     * @param roleName
     * @param permissions
     * @return
     */
    Role roleCreate(String roleName, List<UserPermission> permissions, int powerLevel);

    Role roleSave(Role role);

    Role roleFindById(Long id);

    Role roleFindByRoleName(String roleName);

    List<Role> roleList();

    Role roleUpdate(Role role);

    void roleDeleteById(Long id);

    /**
     * Register new user account
     * 
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param password
     * @param roleName
     * @return
     */
    Account accountRegister(String username, String firstName, String lastName, String email, String phoneNumber,
            String password, String roleName);

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
     * @param newPassword
     * @return
     */
    Account accountChangePassword(Long id, String newPassword);

    void accountSave(Account account);

    Account accountUpdate(Account account);

    Account accountFindWithId(Long id);

    Account accountFindWithUsername(String username);

    Account accountFindWithEmail(String email);

    Account accountFindWithPhoneNumber(String phoneNumber);

    /**
     * Get full list of existing accounts
     * 
     * @return
     */
    List<Account> accountList();

    /**
     * Create & associate a password reset token with an account
     * 
     * @param id
     * @param token
     */
    void createPasswordResetCodeForAccount(Long id, String token);

    /**
     * Find password reset code by account Id
     * 
     * @param id
     * @return
     */
    PasswordResetCode findPasswordResetCodeWithAccountId(Long id);
}
