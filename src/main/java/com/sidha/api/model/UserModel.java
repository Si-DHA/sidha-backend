package com.sidha.api.model;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@SQLDelete(sql = "UPDATE user_table SET is_deleted = true WHERE id=?")
@SQLRestriction( value = "is_deleted = false" )
@Table(name = "user_table")
public class UserModel implements Serializable{
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(name="name", nullable = false)
    private String name;

    @NotNull
    @Column(name="username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Column(name="password", nullable = false)
    private String password;
    
    @NotNull
    @Column(name="email", nullable = false)
    private String email;

    @NotNull
    @Column(name="address", nullable = false)
    private String address;
    
    @NotNull
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @NotNull
    @Column(name="is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
