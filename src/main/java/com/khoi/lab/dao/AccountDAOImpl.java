package com.khoi.lab.dao;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.khoi.lab.entity.Account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class AccountDAOImpl implements AccountDAO {
    private EntityManager em;

    public AccountDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void initiate() {
        System.out.println("| [initiate] Initiating test data.");

        accountRegister("waf", "Le Khoi", "kxyz207@gmail.com", "0793300359", "toilakhoi");
        accountRegister("akari", "Le Khang", "lmkhang165@gmail.com", "0904339600", "lmkhang165");

        accountLogin("waf", "toilakhoi");

        Account acc1 = accountFindWithId(Long.valueOf(1));
        System.out.println(acc1.getLastLoginDate());
    }

    @Override
    @Transactional
    public Account accountRegister(String username, String name, String email, String phoneNumber, String password) {
        Account account = new Account(username, name, email, phoneNumber, password);
        accountSave(account);
        System.out.println("| [accountRegister] Registered account: " + account);
        return account;
    }

    @Override
    public Account accountLogin(String usernameOrEmailOrPhone, String password) {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a WHERE (a.username=:username OR a.email=:email OR a.phoneNumber=:phone) AND a.password=:password",
                Account.class);
        tq.setParameter("username", usernameOrEmailOrPhone);
        tq.setParameter("email", usernameOrEmailOrPhone);
        tq.setParameter("phone", usernameOrEmailOrPhone);
        tq.setParameter("password", password);
        try {
            Account account = tq.getSingleResult();
            System.out.println("| [accountLogin] Details match account: " + account);
            account.setLastLoginDate(new java.sql.Date(System.currentTimeMillis()));
            account = accountUpdate(account);
            return account;
        } catch (NoResultException e) {
            System.out.println("| [accountLogin] Didn't match any account!");
            return null;
        }
    }

    @Override
    @Transactional
    public Account accountChangePassword(Long id, String password, String newPassword, String newPasswordConfirm) {
        Account account = accountFindWithId(id);
        if (account.getPassword() == password && newPassword == newPasswordConfirm) {
            account.setPassword(newPassword);
            account = accountUpdate(account);
        }
        return account;
    }

    @Override
    @Transactional
    public Account accountResetPassword(Long id, String newPassword, String newPasswordConfirm) {
        Account account = accountFindWithId(id);
        if (newPassword == newPasswordConfirm) {
            account.setPassword(newPassword);
            account = accountUpdate(account);
        }
        return account;
    }

    @Override
    @Transactional
    public void accountSave(Account account) {
        em.persist(account);
        System.out.println("| [accountSave] Account saved: " + account);
    }

    @Override
    @Transactional
    public Account accountUpdate(Account account) {
        account = em.merge(account);
        System.out.println("| [accountUpdate] Account updated: " + account);
        return account;
    }

    @Override
    public Account accountFindWithId(Long id) {
        Account account = em.find(Account.class, id);
        if (account == null)
            System.out.println("| [accountFindWithId] Account not found!");
        else
            System.out.println("| [accountFindWithId] Account found: " + account);
        return account;
    }

    @Override
    public Account accountFindWithUsername(String username) {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a WHERE a.username=:username",
                Account.class);
        tq.setParameter("username", username);
        try {
            Account account = tq.getSingleResult();
            System.out.println("| [accountFindWithUsername] Account found: " + account);
            return account;
        } catch (NoResultException e) {
            System.out.println("| [accountFindWithUsername] Account not found!");
            return null;
        }
    }

    @Override
    public List<Account> accountList() {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a",
                Account.class);
        List<Account> accounts = tq.getResultList();
        System.out.println("| [accountList] Found and returned: " + accounts.size() + " accounts!");
        return accounts;
    }
}
