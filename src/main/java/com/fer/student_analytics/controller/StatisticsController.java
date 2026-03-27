package com.fer.student_analytics.controller;

import com.fer.student_analytics.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")

public class StatisticsController { // ovdje nemam CRUD-a i ne vraćam entitete već rezultate izračuna

    private final StatisticsService statisticsService; 

    public StatisticsController(StatisticsService statisticsService) { 
        this.statisticsService = statisticsService;
    }

    // GET /api/statistics/enrollment/{enrollmentId}/total, ukupni bodovi studenta
    @GetMapping("/enrollment/{enrollmentId}/total")
    public ResponseEntity<Double> getTotalPoints(@PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(statisticsService.getTotalPointsForEnrollment(enrollmentId)); // 200 OK
    }

    // GET /api/statistics/group/{groupId}/average, prosjek grupe
    @GetMapping("/group/{groupId}/average")
    public ResponseEntity<Double> getGroupAverage(@PathVariable UUID groupId) {
        return ResponseEntity.ok(statisticsService.getAveragePointsForGroup(groupId)); // 200 OK
    }

    // GET /api/statistics/component/{componentId}/average, prosjek komponente
    @GetMapping("/component/{componentId}/average")
    public ResponseEntity<Double> getComponentAverage(@PathVariable UUID componentId) {
        return ResponseEntity.ok(statisticsService.getAveragePointsForComponent(componentId)); // 200 OK
    }

    // GET /api/statistics/component/{componentId}/obligation, postotak ispunjenih obaveza
    @GetMapping("/component/{componentId}/obligation")
    public ResponseEntity<Double> getObligationPercentage(@PathVariable UUID componentId) {
        return ResponseEntity.ok(statisticsService.getObligationMetPercentage(componentId)); // 200 OK
    }

    // GET /api/statistics/enrollment/{enrollmentId}/exam-access, može li student pristupiti ispitu
    @GetMapping("/enrollment/{enrollmentId}/exam-access")
    public ResponseEntity<Boolean> canAccessExam(@PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(statisticsService.canStudentAccessExam(enrollmentId)); // 200 OK
    }
}