package com.khoi.lab.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.stereotype.Repository;

import com.khoi.lab.data.DefaultRolePermissions;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.PasswordResetCode;
import com.khoi.lab.entity.Role;
import com.khoi.lab.enums.UserPermission;
import com.khoi.lab.service.CryptographyService;

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

        // 1. Create and save roles
        roleCreate("manager", DefaultRolePermissions.getPermissionsForRole("manager"), 4);
        roleCreate("admin", DefaultRolePermissions.getPermissionsForRole("admin"), 3);
        roleCreate("user", DefaultRolePermissions.getPermissionsForRole("user"), 1);
        roleCreate("blog_manager", DefaultRolePermissions.getPermissionsForRole("blog_manager"), 2);
        roleCreate("campaign_manager", DefaultRolePermissions.getPermissionsForRole("campaign_manager"), 2);
        roleCreate("donation_manager", DefaultRolePermissions.getPermissionsForRole("donation_manager"), 2);

        // 2. Register accounts and assign roles
        Account acc1 = accountRegister("waf", "Le", "Khoi", "kxyz207@gmail.com", "0793300359", "toilakhoi", "manager");
        Account acc2 = accountRegister("akari", "Le", "Khang", "lmkhang165@gmail.com", "0904339600", "lmkhang165",
                "admin");
        Account acc3 = accountRegister("imitadora", "Jane", "Doe", "milk.yy2k@gmail.com", "0699201920", "abari11",
                "user");
        Account acc4 = accountRegister("skywalker", "Luke", "Skywalker", "luke.skywalker@jedi.com", "0812345678",
                "force123", "user");
        Account acc5 = accountRegister("vader", "Anakin", "Skywalker", "darth.vader@empire.com", "0823456789",
                "darkside", "user");
        Account acc6 = accountRegister("kenobi", "Obi-Wan", "Kenobi", "obiwan.kenobi@jedi.com", "0834567890",
                "hello_there", "user");
        Account acc7 = accountRegister("yoda", "Master", "Yoda", "master.yoda@jedi.com", "0845678901", "wisdom",
                "user");
        Account acc8 = accountRegister("han", "Han", "Solo", "han.solo@falcon.com", "0856789012", "chewie", "user");
        Account acc9 = accountRegister("leia", "Leia", "Organa", "leia.organa@rebel.com", "0867890123", "princess",
                "blog_manager");
        Account acc10 = accountRegister("chewbacca", "Chew", "Bacca", "chewbacca@falcon.com", "0878901234", "rrraagh",
                "campaign_manager");
        Account acc11 = accountRegister("r2d2", "R2", "D2", "r2.d2@astro.com", "0889012345", "beepboop", "user");
        Account acc12 = accountRegister("c3po", "C-3", "PO", "c3.po@protocol.com", "0890123456", "etiquette",
                "donation_manager");
        Account acc13 = accountRegister("palpatine", "Sheev", "Palpatine", "emperor@empire.com", "0901234567",
                "unlimitedpower", "donation_manager");
        Account acc14 = accountRegister("rey", "Rey", "Skywalker", "rey@jedi.com", "0912345678", "lightforce",
                "user");
        Account acc15 = accountRegister("finn", "FN", "2187", "finn@rebel.com", "0923456789", "freedom", "user");
        Account acc16 = accountRegister("poe", "Poe", "Dameron", "poe.dameron@rebel.com", "0934567890", "blackleader",
                "user");
        Account acc17 = accountRegister("bb8", "BB", "8", "bb.8@astro.com", "0945678901", "rolling", "user");
        Account acc18 = accountRegister("maul", "Darth", "Maul", "darth.maul@sith.com", "0956789012", "doubleblade",
                "user");
        Account acc19 = accountRegister("grievous", "General", "Grievous", "general.grievous@separatist.com",
                "0967890123", "coughcough", "user");
        Account acc20 = accountRegister("ahsoka", "Ahsoka", "Tano", "ahsoka.tano@jedi.com", "0978901234", "snips",
                "admin");
        Account acc21 = accountRegister("mace", "Mace", "Windu", "mace.windu@jedi.com", "0989012345", "purple",
                "user");
        Account acc22 = accountRegister("jango", "Jango", "Fett", "jango.fett@bounty.com", "0990123456", "clonesource",
                "user");
        Account acc23 = accountRegister("boba", "Boba", "Fett", "boba.fett@bounty.com", "0701234567", "sarlacc",
                "user");
        Account acc24 = accountRegister("padme", "Padme", "Amidala", "padme.amidala@senate.com", "0712345678", "queen",
                "user");
        Account acc25 = accountRegister("snoke", "Supreme", "Leader Snoke", "snoke@firstorder.com", "0723456789",
                "darkforce", "user");

        // set details
        acc3.setDisabled(true);
        acc13.setDisabled(true);
        acc15.setDisabled(true);

        acc1.setLastLoginDate(Date.valueOf("2025-07-06"));
        acc2.setLastLoginDate(Date.valueOf("2025-07-06"));
        acc3.setLastLoginDate(Date.valueOf("2025-07-01"));
        acc4.setLastLoginDate(Date.valueOf("2025-07-15"));
        acc5.setLastLoginDate(Date.valueOf("2024-07-01"));
        acc6.setLastLoginDate(Date.valueOf("2024-07-21"));
        acc7.setLastLoginDate(Date.valueOf("2025-07-01"));
        acc8.setLastLoginDate(Date.valueOf("2024-07-11"));
        acc9.setLastLoginDate(Date.valueOf("2024-07-01"));
        acc10.setLastLoginDate(Date.valueOf("2024-07-01"));
        acc11.setLastLoginDate(Date.valueOf("2025-04-01"));
        acc11.setLastLoginDate(Date.valueOf("2025-05-01"));
        acc12.setLastLoginDate(Date.valueOf("2025-01-21"));
        acc13.setLastLoginDate(Date.valueOf("2025-01-10"));
        acc14.setLastLoginDate(Date.valueOf("2025-01-01"));
        acc15.setLastLoginDate(Date.valueOf("2025-01-12"));
        acc16.setLastLoginDate(Date.valueOf("2025-01-01"));
        acc17.setLastLoginDate(Date.valueOf("2023-01-01"));
        acc18.setLastLoginDate(Date.valueOf("2024-01-06"));
        acc19.setLastLoginDate(Date.valueOf("2025-01-01"));
        acc20.setLastLoginDate(Date.valueOf("2025-01-01"));
        acc21.setLastLoginDate(Date.valueOf("2025-01-31"));
        acc22.setLastLoginDate(Date.valueOf("2025-01-23"));
        acc23.setLastLoginDate(Date.valueOf("2023-01-01"));
        acc24.setLastLoginDate(Date.valueOf("2024-01-19"));
        acc25.setLastLoginDate(Date.valueOf("2025-01-17"));
    }

    @Override
    @Transactional
    public Role roleCreate(String roleName, List<UserPermission> permissions, int powerLevel) {
        Role role = new Role(roleName.toLowerCase(), permissions, powerLevel);
        roleSave(role);
        System.out.println("| [roleCreate] Role created: " + role);
        return role;
    }

    @Override
    @Transactional
    public Role roleSave(Role role) {
        em.persist(role);
        System.out.println("| [roleSave] Role saved: " + role);
        return role;
    }

    @Override
    public Role roleFindById(Long id) {
        Role role = em.find(Role.class, id);
        if (role == null) {
            System.out.println("| [roleFindById] Couldn't find role with id: " + id);
        } else {
            System.out.println("| [roleFindById] Found role: " + role);
        }
        return role;
    }

    @Override
    public Role roleFindByRoleName(String roleName) {
        TypedQuery<Role> tq = em.createQuery(
                "SELECT r FROM Role r WHERE r.roleName=:roleName",
                Role.class);
        tq.setParameter("roleName", roleName.toLowerCase());
        try {
            Role role = tq.getSingleResult();
            System.out.println("| [roleFindByRoleName] Role found: " + role);
            return role;
        } catch (NoResultException e) {
            System.out.println("| [roleFindByRoleName] Role not found!");
            return null;
        }
    }

    @Override
    public List<Role> roleList() {
        TypedQuery<Role> tq = em.createQuery(
                "SELECT r FROM Role r",
                Role.class);
        List<Role> roles = tq.getResultList();
        System.out.println("| [roleList] Found and returned: " + roles.size() + " roles!");
        return roles;
    }

    @Override
    @Transactional
    public Role roleUpdate(Role role) {
        role = em.merge(role);
        System.out.println("| [roleUpdate] Role updated: " + role);
        return role;
    }

    @Override
    @Transactional
    public void roleDeleteById(Long id) {
        Role role = roleFindById(id);

        // update accounts' new role to 'user'
        for (Account account : role.getAccounts()) {
            account.setRole(roleFindByRoleName("user"));
            accountUpdate(account);
        }

        em.remove(role);
        System.out.println("| [roleDeleteById] Deleted role with id: " + id);
    }

    @Override
    @Transactional
    public Account accountRegister(String username, String firstName, String lastName, String email, String phoneNumber,
            String password, String roleName) {
        Role role = roleFindByRoleName(roleName);
        Account account = new Account(username, firstName, lastName, email, phoneNumber, password, role);
        accountSave(account);
        System.out.println("| [accountRegister] Registered account: " + account);
        return account;
    }

    @Override
    @Transactional
    public Account accountLogin(String usernameOrEmailOrPhone, String password) {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a WHERE (a.username=:username OR a.email=:email OR a.phoneNumber=:phone) AND a.password=:password",
                Account.class);
        tq.setParameter("username", usernameOrEmailOrPhone);
        tq.setParameter("email", usernameOrEmailOrPhone);
        tq.setParameter("phone", usernameOrEmailOrPhone);
        tq.setParameter("password", CryptographyService.encrypt(password));
        try {
            Account account = tq.getSingleResult();
            System.out.println("| [accountLogin] Details match account: " + account);
            System.out.println("| [accountLogin] Logged in!");
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
    public Account accountChangePassword(Long id, String newPassword) {
        Account account = accountFindWithId(id);
        account.setPassword(newPassword);
        account = accountUpdate(account);
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
    public Account accountFindWithEmail(String email) {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a WHERE a.email=:email",
                Account.class);
        tq.setParameter("email", email);
        try {
            Account account = tq.getSingleResult();
            System.out.println("| [accountFindWithEmail] Account found: " + account);
            return account;
        } catch (NoResultException e) {
            System.out.println("| [accountFindWithEmail] Account not found!");
            return null;
        }
    }

    @Override
    public Account accountFindWithPhoneNumber(String phoneNumber) {
        TypedQuery<Account> tq = em.createQuery(
                "SELECT a FROM Account a WHERE a.phoneNumber=:phoneNumber",
                Account.class);
        tq.setParameter("phoneNumber", phoneNumber);
        try {
            Account account = tq.getSingleResult();
            System.out.println("| [accountFindWithPhoneNumber] Account found: " + account);
            return account;
        } catch (NoResultException e) {
            System.out.println("| [accountFindWithPhoneNumber] Account not found!");
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

    @Override
    @Transactional
    public void createPasswordResetCodeForAccount(Long id, String token) {
        PasswordResetCode code = new PasswordResetCode(token, id);
        em.persist(code);
    }

    @Override
    public PasswordResetCode findPasswordResetCodeWithAccountId(Long id) {
        TypedQuery<PasswordResetCode> tq = em.createQuery(
                "SELECT c FROM PasswordResetCode c WHERE c.accountId=:id",
                PasswordResetCode.class);
        tq.setParameter("id", id);
        try {
            PasswordResetCode code = tq.getSingleResult();
            System.out.println("| [findPasswordResetCodeWithAccountId] Code found: " + code);
            return code;
        } catch (NoResultException e) {
            System.out.println("| [findPasswordResetCodeWithAccountId] Code not found!");
            return null;
        }
    }
}
