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

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) { 
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean // govori Springu da ovu metodu treba pozvati i rezultat registrirati kao bean, ovo mi je bitnooO!!
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
        http 

            // isključujemo CSRF zaštitu jer koristimo JWT tokene, ne session kolačiće
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // koristi moju CORS konfiguraciju
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

            // OPTIONS preflight zahtjevi 
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // javni endpointi, ne trebaju token
            .requestMatchers("/api/auth/**").permitAll()

            // samo admin može upravljati korisnicima
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

            // svi ostali requestovi moraju biti autentificirani
            .anyRequest().authenticated()
        )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();  
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // dopusti sve origine
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false); 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}