package com.khoi.lab.entity;

import com.khoi.lab.enums.UserPermission;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    // This creates a separate table to store the permissions for each role
    @ElementCollection(targetClass = UserPermission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING) // Store the enum as a string
    @Column(name = "permission")
    private List<UserPermission> permissions;

    @OneToMany(mappedBy = "role", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Account> accounts;

    // Constructors
    public Role() {
    }

    public Role(String roleName, List<UserPermission> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<UserPermission> permissions) {
        this.permissions = permissions;
    }

    // The accounts list should not have a public setter to prevent accidental
    // changes
    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public String toString() {
        return "Role [roleName=" + roleName + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
