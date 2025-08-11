package com.khoi.lab.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.Role;
import com.khoi.lab.enums.UserPermission;

@Service
public class UserPermissionService {
    @Autowired
    private AccountDAO accountDAO;

    /**
     * Checks if a user has a specific permission.
     *
     * @param account            The account object of the logged-in user.
     * @param requiredPermission The specific permission to check for.
     * @return true if the account is not null and has the required permission,
     *         false otherwise.
     */
    public boolean hasPermission(Account account, UserPermission requiredPermission) {
        // First, check if the account object is null.
        if (account == null || account.getRole() == null) {
            return false;
        }

        // get roleName
        String roleName = account.getRole().getRoleName();
        Role role = accountDAO.roleFindByRoleName(roleName);

        // role exist check
        if (role == null) {
            return false;
        }

        // get permissions
        List<UserPermission> permissions = role.getPermissions();

        // check if contains required permission
        return permissions.contains(requiredPermission);
    }
}