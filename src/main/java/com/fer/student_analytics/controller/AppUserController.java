package com.fer.student_analytics.controller;

import com.fer.student_analytics.dto.UserResponseDto;
import com.fer.student_analytics.model.SystemUser;
import com.fer.student_analytics.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController // ovo je controller koji vraca JSON
@RequestMapping("/api/users") // sve putanje pocinju sa /api/users
@CrossOrigin(origins = "*") // dopusti React frontendu da poziva ovaj API
public class AppUserController {

    private final AppUserService appUserService; 

    public AppUserController(AppUserService appUserService) { 
        this.appUserService = appUserService;
    }

    // GET /api/users, dohvati sve korisnike
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() { // primijeti da frontendu vraćam DTO, ne SystemUser, jer sakrivam hashlozinku, tako da frontend vidi samo dto
        List<SystemUser> users = appUserService.getAllUsers();
        List<UserResponseDto> response = new ArrayList<>();
        for (SystemUser user : users) {
            response.add(new UserResponseDto(user));
        }
        return ResponseEntity.ok(response); // 200 OK
    }

    // GET /api/users/{id}, dohvati korisnika po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id) {
        Optional<SystemUser> user = appUserService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserResponseDto(user.get())); // 200 OK, mogao sam i sa status body pisat ovo
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/users - kreiraj novog korisnika
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody SystemUser user) {
        SystemUser created = appUserService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto(created)); // 201 Created
    }

    // PUT /api/users/{id} - azuriraj korisnika
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @RequestBody SystemUser user) {
        SystemUser updated = appUserService.updateUser(id, user);
        return ResponseEntity.ok(new UserResponseDto(updated)); // 200 OK, mogao sam i sa status body pisat ovo
    }

    // DELETE /api/users/{id} - obrisi korisnika
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}