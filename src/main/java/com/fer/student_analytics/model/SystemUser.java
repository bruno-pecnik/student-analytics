package com.fer.student_analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data // lombok anotation, it creates getters, setters, toString, equals etc. so I don't have to
@Entity // this tells Spring that this class goes into database as table, without it Spring would ignore this class
@Table(name = "app_user") // table will be called "app_user", i previously named it "system_user" but I got an error because it's reserved name in PostgreSQL

public class SystemUser {

    @Id // this is the primary key for the table records

    @GeneratedValue(strategy = GenerationType.UUID) // Spring will generate ID when I create a user, I don't have to create ID
    private UUID id;

    @Column(nullable = false, unique = true) // nullable=false means this field has to have a value, unique means there can't be duplicates, so email has to be unique and it must exist
    private String email;

    @Column(name = "password_hash", nullable = false) // in database it will be called password_hash
    private String passwordHash;

    @Column(name = "first_name", nullable = false) // in database it will be called first_name
    private String firstName;

    @Column(name = "last_name", nullable = false) // in database it will be called last_name
    private String lastName;

    @Enumerated(EnumType.STRING) // enum save as a String, so instead of STUDENT=0, we save it as "STUDENT", it's more readable, and safer*
    @Column(nullable = false)

    private Role role;

    private String jmbag;

    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum Role {
        STUDENT, PROFESSOR, ADMIN
    }

}
