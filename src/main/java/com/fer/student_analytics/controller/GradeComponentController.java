package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.GradeComponent;
import com.fer.student_analytics.service.GradeComponentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-components")
@CrossOrigin(origins = "*")

public class GradeComponentController {

    private final GradeComponentService gradeComponentService; 

    public GradeComponentController(GradeComponentService gradeComponentService) { 
        this.gradeComponentService = gradeComponentService;
    }

    // GET /api/grade-components, dohvati sve komponente
    @GetMapping
    public ResponseEntity<List<GradeComponent>> getAllComponents() {
        return ResponseEntity.ok(gradeComponentService.getAllComponents()); // 200 OK
    }

    // GET /api/grade-components/by-course/{courseId} - dohvati komponente po kolegiju
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<GradeComponent>> getComponentsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(gradeComponentService.getComponentsByCourse(courseId)); // 200 OK
    }

    // GET /api/grade-components/{id} - dohvati komponentu po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<GradeComponent> getComponentById(@PathVariable UUID id) {
        Optional<GradeComponent> component = gradeComponentService.getComponentById(id);
        if (component.isPresent()) {
            return ResponseEntity.ok(component.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/grade-components, kreiraj novu komponentu
    @PostMapping
    public ResponseEntity<GradeComponent> createComponent(@RequestBody GradeComponent component) {
        GradeComponent created = gradeComponentService.createComponent(component);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // PUT /api/grade-components/{id}, ažuriraj komponentu
    @PutMapping("/{id}")
    public ResponseEntity<GradeComponent> updateComponent(@PathVariable UUID id, @RequestBody GradeComponent component) {
        GradeComponent updated = gradeComponentService.updateComponent(id, component);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/grade-components/{id}, obriši komponentu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable UUID id) {
        gradeComponentService.deleteComponent(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}