package ru.khan.bank.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void promoteToAdmin() {
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("Deleted user cannot become admin");
        }

        this.role = UserRole.ADMIN;
    }

    public User(
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            UserRole role
    ) {
        this.publicId = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        changeStatus(UserStatus.BLOCKED);
    }

    public void activate() {
        changeStatus(UserStatus.ACTIVE);
    }

    public void delete() {
        changeStatus(UserStatus.DELETED);
    }

    private void changeStatus(UserStatus newStatus) {
        validateStatusTransition(newStatus);

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateStatusTransition(UserStatus newStatus) {

        boolean validTransition =
                (status == UserStatus.ACTIVE && newStatus == UserStatus.BLOCKED) ||
                        (status == UserStatus.ACTIVE && newStatus == UserStatus.DELETED) ||
                        (status == UserStatus.BLOCKED && newStatus == UserStatus.ACTIVE) ||
                        (status == UserStatus.BLOCKED && newStatus == UserStatus.DELETED);

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid status transition: " + status + " -> " + newStatus
            );
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}