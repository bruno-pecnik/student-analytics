package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.StudentRecord;
import com.fer.student_analytics.service.StudentRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "*")
public class StudentRecordController {

    private final StudentRecordService studentRecordService; 

    public StudentRecordController(StudentRecordService studentRecordService) { 
        this.studentRecordService = studentRecordService;
    }

    // GET /api/records - dohvati sve rezultate
    @GetMapping
    public ResponseEntity<List<StudentRecord>> getAllRecords() {
        return ResponseEntity.ok(studentRecordService.getAllRecords()); // 200 OK
    }

    // GET /api/records/by-enrollment/{enrollmentId} - dohvati rezultate po upisu
    @GetMapping("/by-enrollment/{enrollmentId}")
    public ResponseEntity<List<StudentRecord>> getRecordsByEnrollment(@PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(studentRecordService.getRecordsByEnrollment(enrollmentId)); // 200 OK
    }

    // GET /api/records/by-component/{componentId} - dohvati rezultate po komponenti
    @GetMapping("/by-component/{componentId}")
    public ResponseEntity<List<StudentRecord>> getRecordsByComponent(@PathVariable UUID componentId) {
        return ResponseEntity.ok(studentRecordService.getRecordsByComponent(componentId)); // 200 OK
    }

    // GET /api/records/{id} - dohvati rezultat po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<StudentRecord> getRecordById(@PathVariable UUID id) {
        Optional<StudentRecord> record = studentRecordService.getRecordById(id);
        if (record.isPresent()) {
            return ResponseEntity.ok(record.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/records - kreiraj novi rezultat
    @PostMapping
    public ResponseEntity<StudentRecord> createRecord(@RequestBody StudentRecord record) {
        StudentRecord created = studentRecordService.createRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // PUT /api/records/{id} - azuriraj rezultat
    @PutMapping("/{id}")
    public ResponseEntity<StudentRecord> updateRecord(@PathVariable UUID id, @RequestBody StudentRecord record) {
        StudentRecord updated = studentRecordService.updateRecord(id, record);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/records/{id} - obrisi rezultat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID id) {
        studentRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}