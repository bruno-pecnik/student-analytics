package com.fer.student_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // lombok generira gettere, setteri nam ne trebaju jer je ovo samo output
@AllArgsConstructor // lombok generira i konstruktor, da postavimo vrijednosti
public class LoginResponseDto {

    // podaci koje vraćamo korisniku nakon uspješnog logina
    private String token;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
}