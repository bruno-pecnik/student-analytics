package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.Course;
import com.fer.student_analytics.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService; 

    public CourseController(CourseService courseService) { 
        this.courseService = courseService;
    }

    // GET /api/courses, dohvati sve kolegije
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses()); // 200 OK
    }

    // GET /api/courses/by-year/{academicYearId}, dohvati kolegije po akademskoj godini
    @GetMapping("/by-year/{academicYearId}") // custom endpoint, API dizajn, daj kolegije za određenu godinu
    public ResponseEntity<List<Course>> getCoursesByAcademicYear(@PathVariable UUID academicYearId) {
        return ResponseEntity.ok(courseService.getCoursesByAcademicYear(academicYearId)); // 200 OK
    }

    // GET /api/courses/{id}, dohvati kolegij po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable UUID id) {
        Optional<Course> course = courseService.getCourseById(id);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/courses, kreiraj novi kolegij
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // PUT /api/courses/{id} - azuriraj kolegij
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable UUID id, @RequestBody Course course) {
        Course updated = courseService.updateCourse(id, course);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/courses/{id} - obrisi kolegij
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}