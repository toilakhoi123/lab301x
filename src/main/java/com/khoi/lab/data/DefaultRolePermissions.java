package com.khoi.lab.data;

import java.util.List;
import java.util.Map;

import com.khoi.lab.enums.UserPermission;

/**
 * Contains data of default role permissions
 */
public class DefaultRolePermissions {
    // This map stores the default permissions for each role.
    public static final Map<String, List<UserPermission>> DEFAULT_ROLE_PERMISSIONS = Map.of(
            "manager", List.of(
                    UserPermission.VIEW_DASHBOARD,
                    UserPermission.MANAGE_USERS,
                    UserPermission.CREATE_ROLES,
                    UserPermission.MANAGE_ROLES,
                    UserPermission.CREATE_CAMPAIGNS,
                    UserPermission.MANAGE_CAMPAIGNS,
                    UserPermission.MANAGE_DONATIONS,
                    UserPermission.CREATE_BLOGS,
                    UserPermission.MANAGE_OWN_BLOGS,
                    UserPermission.MANAGE_BLOGS,
                    UserPermission.CREATE_COMMENTS,
                    UserPermission.MANAGE_OWN_COMMENTS,
                    UserPermission.MANAGE_COMMENTS),
            "admin", List.of(
                    UserPermission.VIEW_DASHBOARD,
                    UserPermission.MANAGE_USERS,
                    UserPermission.CREATE_ROLES,
                    UserPermission.MANAGE_ROLES,
                    UserPermission.CREATE_CAMPAIGNS,
                    UserPermission.MANAGE_CAMPAIGNS,
                    UserPermission.CREATE_DONATIONS,
                    UserPermission.MANAGE_DONATIONS,
                    UserPermission.CREATE_BLOGS,
                    UserPermission.MANAGE_OWN_BLOGS,
                    UserPermission.MANAGE_BLOGS,
                    UserPermission.CREATE_COMMENTS,
                    UserPermission.MANAGE_OWN_COMMENTS,
                    UserPermission.MANAGE_COMMENTS),
            "user", List.of(
                    UserPermission.CREATE_DONATIONS,
                    UserPermission.CREATE_COMMENTS,
                    UserPermission.MANAGE_OWN_COMMENTS),
            "blog_manager", List.of(
                    UserPermission.VIEW_DASHBOARD,
                    UserPermission.CREATE_BLOGS,
                    UserPermission.MANAGE_OWN_BLOGS,
                    UserPermission.MANAGE_BLOGS,
                    UserPermission.CREATE_DONATIONS,
                    UserPermission.CREATE_COMMENTS,
                    UserPermission.MANAGE_OWN_COMMENTS,
                    UserPermission.MANAGE_COMMENTS),
            "campaign_manager", List.of(
                    UserPermission.VIEW_DASHBOARD,
                    UserPermission.CREATE_CAMPAIGNS,
                    UserPermission.MANAGE_CAMPAIGNS),
            "donation_manager", List.of(
                    UserPermission.VIEW_DASHBOARD,
                    UserPermission.CREATE_DONATIONS,
                    UserPermission.MANAGE_DONATIONS));

    /**
     * A helper method to get the permissions for a given role.
     * 
     * @param roleName The name of the role (e.g., "admin", "user").
     * @return A List of UserPermission enums for the role, or an empty list if the
     *         role is not found.
     */
    public static List<UserPermission> getPermissionsForRole(String roleName) {
        return DEFAULT_ROLE_PERMISSIONS.getOrDefault(roleName, List.of());
    }
}
