package com.fer.student_analytics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter // lombok generira gettere i settere
@Setter // setteri nam trebaju jer lombok prima JSON i mora postaviti podatke, ovdje se ne koristi konstruktor
public class LoginRequestDto {

    // podaci koje korisnik šalje pri loginu
    private String email;
    private String password;
}