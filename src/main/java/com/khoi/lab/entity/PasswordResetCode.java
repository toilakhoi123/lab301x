package com.khoi.lab.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Account Forgot password reset code entity
 */
@Entity
@Table(name = "password_reset_code")
public class PasswordResetCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    /**
     * The account this code belongs to
     */
    @Column(name = "account_id")
    private Long accountId;

    protected PasswordResetCode() {
    }

    public PasswordResetCode(String code, Long accountId) {
        this.code = code;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getAccount_id() {
        return accountId;
    }

    public void setAccount_id(Long account_id) {
        this.accountId = account_id;
    }

    @Override
    public String toString() {
        return "PasswordResetCode [code=" + code + ", account_id=" + accountId + "]";
    }
}
