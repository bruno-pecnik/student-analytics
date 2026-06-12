package com.fer.student_analytics.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

import lombok.extern.slf4j.Slf4j; 

@Slf4j // lombok za ispis log.warn

@Component // 
public class JwtUtil {

    @Value("${jwt.secret}") 
    private String secret; 

    @Value("${jwt.expiration}") 
    private long expiration; 

    // generira JWT token za korisnika
    public String generateToken(String email, String role) {
        return Jwts.builder() // kreira novi JWT token
            .subject(email) // email je glavni identifikator korisnika u tokenu
            .claim("role", role) // dodajemo ulogu u token da znamo je li student/profesor/admin
            .issuedAt(new Date()) // kada je token izdan
            .expiration(new Date(System.currentTimeMillis() + expiration)) 
            .signWith(getSigningKey()) // potpisujemo token tajnim ključem
            .compact(); // pretvaramo u string
    }

    // izvlači email iz tokena
    public String extractEmail(String token) {
        return extractClaims(token).getSubject(); // subject je email koji smo stavili pri generiranju
    }

    // izvlači ulogu iz tokena
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class); // uzima vrijednost pod ključem "role", očekujemo da je tipa String
    }

    // provjerava je li token validan i nije istekao
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date()); // uspoređuje vrijeme isteka tokena s trenutnim vremenom, ako je false znači da je token istekao
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false; // ako token nije validan, bacit će exception pa vraćamo false
        }
    }

    private Claims extractClaims(String token) { // private jer samo JwtUtil može koristit ovu metodu
        return Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) getSigningKey()) // verificiramo potpis
            .build() // finalizira parser
            .parseSignedClaims(token) // parsira token
            .getPayload(); // vraća podatke iz tokena
    } // metode su iz jwt biblioteke

    // pretvara tajni string u kriptografski ključ
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(); // pretvori string u niz bajtova
        return Keys.hmacShaKeyFor(keyBytes); // od tihh bajtova napravi HMAC ključ
    } // sad ne može bilo tko fakeat token jer je potpisan ključem
}