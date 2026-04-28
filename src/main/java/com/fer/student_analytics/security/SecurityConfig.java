package com.fer.student_analytics.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration // govori springu da je ovo konfiguracijska klasa
@EnableWebSecurity // uključuje Spring Security za CIJELUI aplikaciju
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // referenca na moj filter koji čita JWT token iz requesta

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) { // konstruktor
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean // govori Springu da ovu metodu treba pozvati i rezultat registrirati kao bean, ovo mi je bitnooO!!
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // SecurityFilterChain je lanac security pravila koji će se primjenjivati na requestove
        http // počinjemo graditi security pravila

            // isključujemo CSRF zaštitu jer koristimo JWT tokene, ne session kolačiće
            // CSRF štiti od napada gdje zlonamjerna stranica šalje zahtjeve u ime korisnika
            // ali JWT tokeni su sigurniji mehanizam pa CSRF nije potreban
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // koristi moju CORS konfiguraciju
            // STATELESS, ne pamtimo session na serveru
            // svaki zahtjev mora nositi JWT token jer server ne pamti tko je ulogiran
            // to je osnova JWT autentikacije
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // pravila pristupa, tko smije što
            .authorizeHttpRequests(auth -> auth 

                // javni endpointi, ne trebaju token, dostupni svima bez tokena (npr. login)
                .requestMatchers("/api/auth/**").permitAll() // login je javan, svi mogu

                // admin endpointi, samo admin može upravljati korisnicima
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // profesor i admin mogu unositi i mijenjati rezultate
                .requestMatchers(HttpMethod.POST, "/api/records/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/records/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/records/**").hasAnyRole("PROFESSOR", "ADMIN")

                // profesor i admin mogu kreirati i mijenjati kolegije
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("PROFESSOR", "ADMIN")

                // profesor i admin mogu kreirati i mijenjati komponente ocjenjivanja
                .requestMatchers(HttpMethod.POST, "/api/grade-components/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/grade-components/**").hasAnyRole("PROFESSOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/grade-components/**").hasAnyRole("PROFESSOR", "ADMIN")


                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // dopusti preflight zahtjeve
                
                // svi ostali requestovi moraju biti autentificirani (ulogirani)
                .anyRequest().authenticated()

                
            )

            // dodaj moj JWT filter PRIJE Spring Security defaultnog filtera
            // tako Spring Security koristi moj token umjesto defaultne forme za prijavu
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();  // gotov sam sa konfiguracijom, izgradi security chain
    }

    @Bean // CORS konfiguracija
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://student-analytics-frontend-k365cezqh-bruno-pecniks-projects.vercel.app"
        ));        
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}