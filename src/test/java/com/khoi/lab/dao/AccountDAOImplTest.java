package com.khoi.lab.dao;

import com.khoi.lab.data.DefaultRolePermissions;
import com.khoi.lab.entity.Account;
import com.khoi.lab.entity.PasswordResetCode;
import com.khoi.lab.entity.Role;
import com.khoi.lab.enums.UserPermission;
import com.khoi.lab.service.CryptographyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AccountDAOImpl class using Mockito.
 * This class isolates the DAO by mocking the EntityManager and other
 * dependencies,
 * ensuring fast and reliable tests without a real database.
 */
@ExtendWith(MockitoExtension.class)
class AccountDAOImplTest {
    // Mocking the dependencies
    @Mock
    private EntityManager mockEm;

    // Mocking the TypedQuery objects for various return types
    @Mock
    private TypedQuery<Account> mockAccountTypedQuery;

    @Mock
    private TypedQuery<Role> mockRoleTypedQuery;

    @Mock
    private TypedQuery<PasswordResetCode> mockPasswordResetCodeTypedQuery;

    // Injecting the mocked dependencies into the DAO implementation
    @InjectMocks
    private AccountDAOImpl accountDAO;

    // Mock data for testing
    private Role mockRole;
    private Account mockAccount;
    private PasswordResetCode mockCode;

    @BeforeEach
    void setUp() {
        // Create mock objects for common use cases
        mockRole = new Role("user", Collections.singletonList(UserPermission.CREATE_DONATIONS), 1);
        mockRole.setId(1L);

        mockAccount = new Account("testuser", "Test", "User", "test@example.com", "1234567890", "password", mockRole);
        mockAccount.setId(1L);

        mockCode = new PasswordResetCode("test_code", 1L);
        mockCode.setId(1L);
    }

    // --- Role Tests ---

    @Test
    void testRoleCreate() {
        try (MockedStatic<DefaultRolePermissions> mockedDPR = mockStatic(DefaultRolePermissions.class)) {
            // Arrange
            String roleName = "test_role";
            List<UserPermission> permissions = Collections.emptyList();
            int powerLevel = 1;
            Role newRole = new Role(roleName, permissions, powerLevel);
            newRole.setRoleName(roleName.toLowerCase()); // The method lowercases the name

            mockedDPR.when(() -> DefaultRolePermissions.getPermissionsForRole(anyString())).thenReturn(permissions);

            // Mock behavior for em.persist()
            doNothing().when(mockEm).persist(any(Role.class));

            // Act
            Role createdRole = accountDAO.roleCreate(roleName, permissions, powerLevel);

            // Assert
            assertNotNull(createdRole);
            assertEquals(roleName.toLowerCase(), createdRole.getRoleName());
            assertEquals(permissions, createdRole.getPermissions());
            assertEquals(powerLevel, createdRole.getPowerLevel());
            verify(mockEm, times(1)).persist(any(Role.class));
        }
    }

    @Test
    void testRoleSave() {
        // Arrange
        Role roleToSave = new Role("new_role", Collections.emptyList());
        doNothing().when(mockEm).persist(roleToSave);

        // Act
        Role savedRole = accountDAO.roleSave(roleToSave);

        // Assert
        assertNotNull(savedRole);
        assertEquals(roleToSave, savedRole);
        verify(mockEm, times(1)).persist(roleToSave);
    }

    @Test
    void testRoleFindById_Success() {
        // Arrange
        when(mockEm.find(Role.class, 1L)).thenReturn(mockRole);

        // Act
        Role foundRole = accountDAO.roleFindById(1L);

        // Assert
        assertNotNull(foundRole);
        assertEquals(mockRole, foundRole);
        verify(mockEm, times(1)).find(Role.class, 1L);
    }

    @Test
    void testRoleFindById_NotFound() {
        // Arrange
        when(mockEm.find(Role.class, 999L)).thenReturn(null);

        // Act
        Role foundRole = accountDAO.roleFindById(999L);

        // Assert
        assertNull(foundRole);
        verify(mockEm, times(1)).find(Role.class, 999L);
    }

    @Test
    void testRoleFindByRoleName_Success() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Role.class))).thenReturn(mockRoleTypedQuery);
        when(mockRoleTypedQuery.setParameter("roleName", "user")).thenReturn(mockRoleTypedQuery);
        when(mockRoleTypedQuery.getSingleResult()).thenReturn(mockRole);

        // Act
        Role foundRole = accountDAO.roleFindByRoleName("user");

        // Assert
        assertNotNull(foundRole);
        assertEquals(mockRole, foundRole);
        verify(mockEm).createQuery("SELECT r FROM Role r WHERE r.roleName=:roleName", Role.class);
        verify(mockRoleTypedQuery).setParameter("roleName", "user");
        verify(mockRoleTypedQuery).getSingleResult();
    }

    @Test
    void testRoleFindByRoleName_NotFound() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Role.class))).thenReturn(mockRoleTypedQuery);
        when(mockRoleTypedQuery.setParameter("roleName", "non_existent")).thenReturn(mockRoleTypedQuery);
        when(mockRoleTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Role foundRole = accountDAO.roleFindByRoleName("non_existent");

        // Assert
        assertNull(foundRole);
        verify(mockEm).createQuery("SELECT r FROM Role r WHERE r.roleName=:roleName", Role.class);
        verify(mockRoleTypedQuery).setParameter("roleName", "non_existent");
        verify(mockRoleTypedQuery).getSingleResult();
    }

    @Test
    void testRoleList() {
        // Arrange
        List<Role> expectedRoles = List.of(mockRole, new Role("admin", Collections.emptyList()));
        when(mockEm.createQuery(anyString(), eq(Role.class))).thenReturn(mockRoleTypedQuery);
        when(mockRoleTypedQuery.getResultList()).thenReturn(expectedRoles);

        // Act
        List<Role> roles = accountDAO.roleList();

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals(expectedRoles, roles);
        verify(mockEm).createQuery("SELECT r FROM Role r", Role.class);
        verify(mockRoleTypedQuery).getResultList();
    }

    @Test
    void testRoleUpdate() {
        // Arrange
        Role roleToUpdate = new Role("updated_role", Collections.emptyList());
        when(mockEm.merge(roleToUpdate)).thenReturn(roleToUpdate);

        // Act
        Role updatedRole = accountDAO.roleUpdate(roleToUpdate);

        // Assert
        assertNotNull(updatedRole);
        assertEquals(roleToUpdate, updatedRole);
        verify(mockEm).merge(roleToUpdate);
    }

    // --- Account Tests ---

    @Test
    void testAccountLogin_SuccessWithUsername() {
        // Arrange
        try (MockedStatic<CryptographyService> mockedCrypto = mockStatic(CryptographyService.class)) {
            String username = "testuser";
            String password = "password";
            String encryptedPassword = "encrypted_password";

            mockedCrypto.when(() -> CryptographyService.encrypt(password)).thenReturn(encryptedPassword);

            when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.setParameter("username", username)).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.setParameter("email", username)).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.setParameter("phone", username)).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.setParameter("password", encryptedPassword)).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.getSingleResult()).thenReturn(mockAccount);
            when(mockEm.merge(any(Account.class))).thenReturn(mockAccount);

            // Act
            Account loggedInAccount = accountDAO.accountLogin(username, password);

            // Assert
            assertNotNull(loggedInAccount);
            assertEquals(mockAccount, loggedInAccount);
            assertNotNull(loggedInAccount.getLastLoginDate());
            verify(mockAccountTypedQuery).getSingleResult();
        }
    }

    @Test
    void testAccountLogin_NotFound() {
        // Arrange
        try (MockedStatic<CryptographyService> mockedCrypto = mockStatic(CryptographyService.class)) {
            String username = "nonexistent";
            String password = "password";
            String encryptedPassword = "encrypted_password";
            mockedCrypto.when(() -> CryptographyService.encrypt(password)).thenReturn(encryptedPassword);

            when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
            when(mockAccountTypedQuery.getSingleResult()).thenThrow(new NoResultException());

            // Act
            Account loggedInAccount = accountDAO.accountLogin(username, password);

            // Assert
            assertNull(loggedInAccount);
            verify(mockAccountTypedQuery).getSingleResult();
        }
    }

    @Test
    void testAccountSave() {
        // Arrange
        doNothing().when(mockEm).persist(mockAccount);

        // Act
        accountDAO.accountSave(mockAccount);

        // Assert
        verify(mockEm, times(1)).persist(mockAccount);
    }

    @Test
    void testAccountUpdate() {
        // Arrange
        when(mockEm.merge(mockAccount)).thenReturn(mockAccount);

        // Act
        Account updatedAccount = accountDAO.accountUpdate(mockAccount);

        // Assert
        assertNotNull(updatedAccount);
        assertEquals(mockAccount, updatedAccount);
        verify(mockEm, times(1)).merge(mockAccount);
    }

    @Test
    void testAccountFindWithId_Success() {
        // Arrange
        when(mockEm.find(Account.class, 1L)).thenReturn(mockAccount);

        // Act
        Account foundAccount = accountDAO.accountFindWithId(1L);

        // Assert
        assertNotNull(foundAccount);
        assertEquals(mockAccount, foundAccount);
        verify(mockEm, times(1)).find(Account.class, 1L);
    }

    @Test
    void testAccountFindWithId_NotFound() {
        // Arrange
        when(mockEm.find(Account.class, 999L)).thenReturn(null);

        // Act
        Account foundAccount = accountDAO.accountFindWithId(999L);

        // Assert
        assertNull(foundAccount);
        verify(mockEm, times(1)).find(Account.class, 999L);
    }

    @Test
    void testAccountFindWithUsername_Success() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("username", "testuser")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenReturn(mockAccount);

        // Act
        Account foundAccount = accountDAO.accountFindWithUsername("testuser");

        // Assert
        assertNotNull(foundAccount);
        assertEquals(mockAccount, foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.username=:username", Account.class);
        verify(mockAccountTypedQuery).setParameter("username", "testuser");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountFindWithUsername_NotFound() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("username", "nonexistent")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Account foundAccount = accountDAO.accountFindWithUsername("nonexistent");

        // Assert
        assertNull(foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.username=:username", Account.class);
        verify(mockAccountTypedQuery).setParameter("username", "nonexistent");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountFindWithEmail_Success() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("email", "test@example.com")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenReturn(mockAccount);

        // Act
        Account foundAccount = accountDAO.accountFindWithEmail("test@example.com");

        // Assert
        assertNotNull(foundAccount);
        assertEquals(mockAccount, foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.email=:email", Account.class);
        verify(mockAccountTypedQuery).setParameter("email", "test@example.com");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountFindWithEmail_NotFound() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("email", "nonexistent@example.com")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Account foundAccount = accountDAO.accountFindWithEmail("nonexistent@example.com");

        // Assert
        assertNull(foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.email=:email", Account.class);
        verify(mockAccountTypedQuery).setParameter("email", "nonexistent@example.com");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountFindWithPhoneNumber_Success() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("phoneNumber", "1234567890")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenReturn(mockAccount);

        // Act
        Account foundAccount = accountDAO.accountFindWithPhoneNumber("1234567890");

        // Assert
        assertNotNull(foundAccount);
        assertEquals(mockAccount, foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.phoneNumber=:phoneNumber", Account.class);
        verify(mockAccountTypedQuery).setParameter("phoneNumber", "1234567890");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountFindWithPhoneNumber_NotFound() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.setParameter("phoneNumber", "0000000000")).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Account foundAccount = accountDAO.accountFindWithPhoneNumber("0000000000");

        // Assert
        assertNull(foundAccount);
        verify(mockEm).createQuery("SELECT a FROM Account a WHERE a.phoneNumber=:phoneNumber", Account.class);
        verify(mockAccountTypedQuery).setParameter("phoneNumber", "0000000000");
        verify(mockAccountTypedQuery).getSingleResult();
    }

    @Test
    void testAccountList() {
        // Arrange
        List<Account> expectedAccounts = List.of(mockAccount, new Account());
        when(mockEm.createQuery(anyString(), eq(Account.class))).thenReturn(mockAccountTypedQuery);
        when(mockAccountTypedQuery.getResultList()).thenReturn(expectedAccounts);

        // Act
        List<Account> accounts = accountDAO.accountList();

        // Assert
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
        assertEquals(expectedAccounts, accounts);
        verify(mockEm).createQuery("SELECT a FROM Account a", Account.class);
        verify(mockAccountTypedQuery).getResultList();
    }

    @Test
    void testCreatePasswordResetCodeForAccount() {
        // Arrange
        Long accountId = 1L;
        String token = "test_token";
        doNothing().when(mockEm).persist(any(PasswordResetCode.class));

        // Act
        accountDAO.createPasswordResetCodeForAccount(accountId, token);

        // Assert
        verify(mockEm, times(1)).persist(any(PasswordResetCode.class));
    }

    @Test
    void testFindPasswordResetCodeWithAccountId_Success() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(PasswordResetCode.class))).thenReturn(mockPasswordResetCodeTypedQuery);
        when(mockPasswordResetCodeTypedQuery.setParameter("id", 1L)).thenReturn(mockPasswordResetCodeTypedQuery);
        when(mockPasswordResetCodeTypedQuery.getSingleResult()).thenReturn(mockCode);

        // Act
        PasswordResetCode foundCode = accountDAO.findPasswordResetCodeWithAccountId(1L);

        // Assert
        assertNotNull(foundCode);
        assertEquals(mockCode, foundCode);
        verify(mockEm).createQuery("SELECT c FROM PasswordResetCode c WHERE c.accountId=:id", PasswordResetCode.class);
        verify(mockPasswordResetCodeTypedQuery).setParameter("id", 1L);
        verify(mockPasswordResetCodeTypedQuery).getSingleResult();
    }

    @Test
    void testFindPasswordResetCodeWithAccountId_NotFound() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(PasswordResetCode.class))).thenReturn(mockPasswordResetCodeTypedQuery);
        when(mockPasswordResetCodeTypedQuery.setParameter("id", 999L)).thenReturn(mockPasswordResetCodeTypedQuery);
        when(mockPasswordResetCodeTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        PasswordResetCode foundCode = accountDAO.findPasswordResetCodeWithAccountId(999L);

        // Assert
        assertNull(foundCode);
        verify(mockEm).createQuery("SELECT c FROM PasswordResetCode c WHERE c.accountId=:id", PasswordResetCode.class);
        verify(mockPasswordResetCodeTypedQuery).setParameter("id", 999L);
        verify(mockPasswordResetCodeTypedQuery).getSingleResult();
    }
}
