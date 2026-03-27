package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.AcademicYear;
import com.fer.student_analytics.service.AcademicYearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController // kaze springu da ova klasa prima web requestove i vraća JSON response, a ne HTML stranicu
@RequestMapping("/api/academic-years") // svi endpointi u klasi počinju sa ovime
@CrossOrigin(origins = "*") // dopušta da frontend sa drugog URL-a može zvati backend
public class AcademicYearController {

    private final AcademicYearService academicYearService; // referenca na service

    public AcademicYearController(AcademicYearService academicYearService) { // konstruktor
        this.academicYearService = academicYearService;
    }

    // GET /api/academic-years, dohvati sve akademske godine
    @GetMapping // ova metoda odgovara na HTTP GET zahtjev
    public ResponseEntity<List<AcademicYear>> getAllAcademicYears() { // responseEntity ima status i body
        return ResponseEntity.status(HttpStatus.OK).body(academicYearService.getAllAcademicYears()); // 200 OK
    }

    // GET /api/academic-years/{id}, dohvati akademsku godinu po ID-u
    @GetMapping("/{id}") // dodatno uzima ID iz URL-a
    public ResponseEntity<AcademicYear> getAcademicYearById(@PathVariable UUID id) { // pretvori ID iz URL-a u java varijablu ID
        Optional<AcademicYear> academicYear = academicYearService.getAcademicYearById(id);
        if (academicYear.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(academicYear.get()); // 200 OK ako postoji
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji, build znaci bez body-a
    }

    // POST /api/academic-years, kreiraj novu akademsku godinu
    @PostMapping
    public ResponseEntity<AcademicYear> createAcademicYear(@RequestBody AcademicYear academicYear) { // spring frontendov request pretvori u ovaj parametar
        AcademicYear createdAcademicYear = academicYearService.createAcademicYear(academicYear); // poziva metodu iz service sloja koja kreira akademsku godinu
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAcademicYear); // 201 Created, vratili smo response entity sa tim stsatusom i tim bodyem
    }

    // PUT /api/academic-years/{id}, ažuriraj akademsku godinu
    @PutMapping("/{id}")
    public ResponseEntity<AcademicYear> updateAcademicYear(@PathVariable UUID id, @RequestBody AcademicYear academicYear) {
        AcademicYear updatedAcademicYear = academicYearService.updateAcademicYear(id, academicYear);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAcademicYear); // 200 ok
    }

    // DELETE /api/academic-years/{id}, obrisi akademsku godinu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable UUID id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.noContent().build(); // 204 No Content, vraća uspjeh, ali bez podatka, .build() napravi response bez podataka
    }
}