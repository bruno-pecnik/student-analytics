package com.fer.student_analytics.service;

import com.fer.student_analytics.dto.LoginRequestDto;
import com.fer.student_analytics.dto.LoginResponseDto;
import com.fer.student_analytics.model.SystemUser;
import com.fer.student_analytics.repository.AppUserRepository;
import com.fer.student_analytics.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository; // referenca na bazu
    private final JwtUtil jwtUtil; // referenca na jwt klasu koja barata tokenima

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // gotova klasa iz Spring Securitya za usporedbu lozinke

    public AuthService(AppUserRepository userRepository, JwtUtil jwtUtil) { // konstruktor
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponseDto login(LoginRequestDto request) { // prima login podatke i vraća odgovor 

        // normalizira email prije traženja u bazi
        String email = request.getEmail().toLowerCase().trim();

        // pronađe korisnika po emailu
        SystemUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Pogrešan email ili lozinka!")); // poruka je ovakva radi sigurnosti, da se ne zna je li pogrešan email ili lozinka

        // provjeri lozinku, uspoređujemo upisanu lozinku s hashom u bazi
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Pogrešan email ili lozinka!"); // namjerno ista poruka, ne otkrivamo je li email ili lozinka krivi
        }

        // generira JWT token za korisnika
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // objekt koji se šalje frontendu kao odgovor
        return new LoginResponseDto(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getFirstName(),
            user.getLastName()
        );
    }
}