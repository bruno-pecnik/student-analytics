package com.fer.student_analytics.repository;

import com.fer.student_analytics.model.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository 
// we inherit all the methods from JpaRepositoryja. SystemUser = table I am working with, UUID = primary key type
public interface AppUserRepository extends JpaRepository<SystemUser, UUID> {
    Optional<SystemUser> findByEmail(String email); // my custom method
}