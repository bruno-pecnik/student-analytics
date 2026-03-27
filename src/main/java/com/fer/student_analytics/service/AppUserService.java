package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.SystemUser;
import com.fer.student_analytics.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    private final AppUserRepository userRepository; // referenca na bazu

    public AppUserService(AppUserRepository userRepository) { // konstruktor
        this.userRepository = userRepository;
    }

    public List<SystemUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<SystemUser> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<SystemUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email); // custom spring metoda koju sam definirao imenom pa spring automatski napravi SQL iz imena
    }

    public SystemUser createUser(SystemUser user) { // prvo normaliziram, pa validiram, pa spremam
        user.setFirstName(normalizeAndValidateName(user.getFirstName(), "Ime"));
        user.setLastName(normalizeAndValidateName(user.getLastName(), "Prezime"));

        user.setEmail(user.getEmail() != null ? user.getEmail().toLowerCase().trim() : null); // normaliziram, email je case insensitive

        validateEmail(user.getEmail(), null);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public SystemUser updateUser(UUID id, SystemUser updated) {
        SystemUser existing = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa ID: " + id + " nije pronađen."));

        existing.setFirstName(normalizeAndValidateName(updated.getFirstName(), "Ime"));
        existing.setLastName(normalizeAndValidateName(updated.getLastName(), "Prezime"));

        String newEmail = updated.getEmail() != null ?
            updated.getEmail().toLowerCase().trim() : null; // normaliziram, email je case insensitive

        validateEmail(newEmail, id); // proslijeđujemo currentUserId da ne blokira vlastiti email

        existing.setEmail(newEmail);
        existing.setJmbag(updated.getJmbag());
        existing.setAvatarUrl(updated.getAvatarUrl());
        return userRepository.save(existing);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Korisnik sa ID: " + id + " nije pronađen.");
        }
        userRepository.deleteById(id);
    }

    private void validateEmail(String email, UUID currentUserId) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email je obavezan."); // email je obavezan
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@(fer\\.hr|fer\\.unizg\\.hr)$";

        if (!email.matches(emailRegex)) { // email mora biti fer domena
            throw new IllegalArgumentException("Email mora biti FER domena.");
        }

        Optional<SystemUser> existing = userRepository.findByEmail(email);

        if (existing.isPresent() && // email ne smije biti zauzet, isPresent je iz java biblioteke
           (currentUserId == null || !existing.get().getId().equals(currentUserId))) {
           throw new IllegalArgumentException("Željena email adresa je zauzeta! Upišite novu email adresu.");
        }
    }

    private String normalizeAndValidateName(String value, String fieldName) {

        if (value == null) {
            throw new IllegalArgumentException(fieldName + " je obavezno."); // ime i prezime su obavezni
        }

        value = value.trim(); // uklanjanje praznina prije i poslije

        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " je obavezno."); // ime i prezime su obavezni
        }

        return value;
    }
}