package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.GradeRule;
import com.fer.student_analytics.service.GradeRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-rules")
@CrossOrigin(origins = "*")
public class GradeRuleController {

    private final GradeRuleService gradeRuleService; 

    public GradeRuleController(GradeRuleService gradeRuleService) { 
        this.gradeRuleService = gradeRuleService;
    }

    // GET /api/grade-rules, dohvati sva pravila
    @GetMapping
    public ResponseEntity<List<GradeRule>> getAllGradeRules() {
        return ResponseEntity.ok(gradeRuleService.getAllGradeRules()); // 200 OK
    }

    // GET /api/grade-rules/by-course/{courseId} - dohvati pravila po kolegiju
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<GradeRule>> getGradeRulesByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(gradeRuleService.getGradeRulesByCourse(courseId)); // 200 OK
    }

    // GET /api/grade-rules/{id} - dohvati pravilo po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<GradeRule> getGradeRuleById(@PathVariable UUID id) {
        Optional<GradeRule> gradeRule = gradeRuleService.getGradeRuleById(id);
        if (gradeRule.isPresent()) {
            return ResponseEntity.ok(gradeRule.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/grade-rules - kreiraj novo pravilo
    @PostMapping
    public ResponseEntity<GradeRule> createGradeRule(@RequestBody GradeRule gradeRule) {
        GradeRule created = gradeRuleService.createGradeRule(gradeRule);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // PUT /api/grade-rules/{id} - ažuriraj pravilo
    @PutMapping("/{id}")
    public ResponseEntity<GradeRule> updateGradeRule(@PathVariable UUID id, @RequestBody GradeRule gradeRule) {
        GradeRule updated = gradeRuleService.updateGradeRule(id, gradeRule);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/grade-rules/{id}, obriši pravilo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGradeRule(@PathVariable UUID id) {
        gradeRuleService.deleteGradeRule(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}