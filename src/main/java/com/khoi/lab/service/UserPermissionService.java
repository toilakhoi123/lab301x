package com.khoi.lab.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.khoi.lab.data.DefaultRolePermissions;
import com.khoi.lab.entity.Account;
import com.khoi.lab.enums.UserPermission;

@Service
public class UserPermissionService {
    /**
     * Checks if a user has a specific permission.
     *
     * @param account            The account object of the logged-in user.
     * @param requiredPermission The specific permission to check for.
     * @return true if the account is not null and has the required permission,
     *         false otherwise.
     */
    public boolean hasPermission(Account account, UserPermission requiredPermission) {
        // First, check if the account object is null
        if (account == null) {
            return false;
        }

        // Get the role string from the account object.
        String roleName = account.getRole().getRoleName();

        // Get the list of permissions for that role from our manager.
        List<UserPermission> permissions = DefaultRolePermissions.getPermissionsForRole(roleName);

        // If the role doesn't exist or has no permissions, return false.
        if (permissions == null) {
            return false;
        }

        // Check if the list of permissions contains the required permission.
        return permissions.contains(requiredPermission);
    }
}