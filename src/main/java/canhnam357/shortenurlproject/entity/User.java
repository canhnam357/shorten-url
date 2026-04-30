package canhnam357.shortenurlproject.entity;

import canhnam357.shortenurlproject.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private Boolean enabled = false;

    @Builder.Default
    private Boolean locked = false;

    private ZonedDateTime createdAt;

    private ZonedDateTime lastLoginAt;

    private ZonedDateTime lastPasswordResetAt;

    @PrePersist
    void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
