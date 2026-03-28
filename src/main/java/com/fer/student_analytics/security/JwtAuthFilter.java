package com.fer.student_analytics.security;

import com.fer.student_analytics.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Slf4j // lombok
@Component // Spring kreira ovjekt ove klase i može je ubacivati u druge klase, konkretno u SecurityConfig

public class JwtAuthFilter extends OncePerRequestFilter { 
    // OncePerRequestFilter je Spring klasa koja osigurava da se filter izvršava točno jednom po zahtjevu
    // bez toga bi se filter mogoa izvršiti više puta za isti zahtjev

    private final JwtUtil jwtUtil; // referenca
    private final AppUserRepository userRepository; // referenca na bazu korisnika

    public JwtAuthFilter(JwtUtil jwtUtil, AppUserRepository userRepository) { // konstruktor
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,   // dolazni HTTP zahtjev
            HttpServletResponse response, // odlazni HTTP odgovor
            FilterChain filterChain)      // lanac filtera, pozivamo ga kad završimo
            throws ServletException, IOException {

        // uzimamo header iz zahtjeva, to je java biblioteka
        String authHeader = request.getHeader("Authorization");

        // ako nema Authorization headera ili ne počinje s "Bearer " zahtjev prolazi dalje bez autentikacije, npr. request dođe bez tokena
        if (!hasValidBearerHeader(authHeader)) {
            log.warn("Zahtjev bez Authorization headera: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // makni "Bearer " prefiks, tj. izvuci token
        String token = extractToken(authHeader);

        // provjeri je li token validan / nije istekao
        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Nevalidan JWT token za zahtjev: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // izvuci email i ulogu iz tokena
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // provjeri postoji li korisnik još uvijek u bazi
        if (!userExists(email)) {
            log.warn("Korisnik iz tokena ne postoji u bazi: {}", email);
            filterChain.doFilter(request, response); // ako korisnik ne postoji, pustim request dalje, ali bez autentifikacije, npr. za login
            return;
        }

        // kreiraj Spring Security autentikaciju
        UsernamePasswordAuthenticationToken authentication = createAuthentication(email, role);

        // postavi autentikaciju u Spring Security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Autentificiran korisnik: {}, s ulogom: {}", email, role);

        // nastavi s obradom zahtjeva prema Controlleru, tj. prosljeđuje request sljedećem filtru
        filterChain.doFilter(request, response);
    }

    private boolean hasValidBearerHeader(String authHeader) { // provjerava je li header tokena dobar
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private String extractToken(String authHeader) { // prima cijeli token, vraća samo token
        return authHeader.substring(7);
    }

    private boolean userExists(String email) { // provjerava postoji li korisnik po emailu
        return userRepository.findByEmail(email).isPresent();
    }

    private UsernamePasswordAuthenticationToken createAuthentication(String email, String role) { // kreira ulogiranog korisnika za spring security
        return new UsernamePasswordAuthenticationToken(
            email, // preko emaila identificiramo korisnika
            null,  // lozinka nam ovdje ne treba
            List.of(new SimpleGrantedAuthority("ROLE_" + role)) // pretvori role u format koji spring security razumije
        );
    }
}
