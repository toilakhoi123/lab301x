package com.khoi.lab.enums;

/**
 * User permission enum
 */
public enum UserPermission {
    // User management permissions
    MANAGE_USERS,
    CREATE_ROLES,
    MANAGE_ROLES,

    // Campaign management permissions
    CREATE_CAMPAIGNS,
    MANAGE_CAMPAIGNS,

    // Donation management permissions
    CREATE_DONATIONS,
    MANAGE_DONATIONS,

    // Blog permissions
    CREATE_BLOGS,
    MANAGE_OWN_BLOGS,
    MANAGE_BLOGS,

    // Comment permissions
    CREATE_COMMENTS,
    MANAGE_OWN_COMMENTS,
    MANAGE_COMMENTS,
}
