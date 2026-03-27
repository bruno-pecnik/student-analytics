package com.fer.student_analytics.dto;

import com.fer.student_analytics.model.SystemUser;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

// getteri, Spring treba getters za serijalizaciju u JSON
@Getter

public class UserResponseDto { // DTO je filtrirana verzija entity-ja za frontend

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String jmbag;
    private String avatarUrl;
    private LocalDateTime createdAt;

    // konstruktor koji prima SystemUser i kopira polja bez passwordHash
    public UserResponseDto(SystemUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.jmbag = user.getJmbag();
        this.avatarUrl = user.getAvatarUrl();
        this.createdAt = user.getCreatedAt();
    }

    // lombok sa @Getter je bolje nego sve ovo pisati : 
    /* 
        public UUID getId() { return id; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getRole() { return role; }
        public String getJmbag() { return jmbag; }
        public String getAvatarUrl() { return avatarUrl; }
        public LocalDateTime getCreatedAt() { return createdAt; } 
    */

}