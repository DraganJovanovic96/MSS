package com.mss.enumeration;

import lombok.Getter;

/**
 * Enumeration representing permissions in the system.
 */
public enum Permission {
    /**
     * Permission to read.
     */
    ADMIN_READ("admin:read"),

    /**
     * Permission to update.
     */
    ADMIN_UPDATE("admin:update"),

    /**
     * Permission to create.
     */
    ADMIN_CREATE("admin:create"),

    /**
     * Permission to delete.
     */
    ADMIN_DELETE("admin:delete"),

    /**
     * Permission to read.
     */
    USER_READ("user:read"),

    /**
     * Permission to update.
     */
    USER_UPDATE("user:update"),

    /**
     * Permission to create.
     */
    USER_CREATE("user:create"),

    /**
     * Permission to delete.
     */
    USER_DELETE("user:delete"),

    /**
     * Permission to read vacation requests.
     */
    VACATION_READ("vacation:read"),

    /**
     * Permission to create vacation requests.
     */
    VACATION_CREATE("vacation:create"),

    /**
     * Permission to update vacation requests.
     */
    VACATION_UPDATE("vacation:update"),

    /**
     * Permission to delete vacation requests.
     */
    VACATION_DELETE("vacation:delete"),

    /**
     * Permission to read customer reports.
     */
    CUSTOMER_REPORT_READ("customer_report:read"),

    /**
     * Permission to create customer reports.
     */
    CUSTOMER_REPORT_CREATE("customer_report:create"),

    /**
     * Permission to update customer reports.
     */
    CUSTOMER_REPORT_UPDATE("customer_report:update"),

    /**
     * Permission to delete customer reports.
     */
    CUSTOMER_REPORT_DELETE("customer_report:delete");

    /**
     * The string representation of the permission.
     */
    @Getter
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}
