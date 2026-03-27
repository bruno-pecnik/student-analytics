package com.fer.student_analytics.controller;

import com.fer.student_analytics.model.CourseGroup;
import com.fer.student_analytics.service.CourseGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class CourseGroupController {

    private final CourseGroupService courseGroupService; 

    public CourseGroupController(CourseGroupService courseGroupService) { 
        this.courseGroupService = courseGroupService;
    }

    // GET /api/groups, dohvati sve grupe
    @GetMapping
    public ResponseEntity<List<CourseGroup>> getAllGroups() {
        return ResponseEntity.ok(courseGroupService.getAllGroups()); // 200 OK
    }

    // GET /api/groups/by-course/{courseId}, dohvati grupe po kolegiju
    @GetMapping("/by-course/{courseId}") // custom, API dizajn
    public ResponseEntity<List<CourseGroup>> getGroupsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseGroupService.getGroupsByCourse(courseId)); // 200 OK
    }

    // GET /api/groups/{id}, dohvati grupu po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<CourseGroup> getGroupById(@PathVariable UUID id) {
        Optional<CourseGroup> group = courseGroupService.getGroupById(id);
        if (group.isPresent()) {
            return ResponseEntity.ok(group.get()); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 ako ne postoji
    }

    // POST /api/groups, kreiraj novu grupu
    @PostMapping
    public ResponseEntity<CourseGroup> createGroup(@RequestBody CourseGroup group) {
        CourseGroup created = courseGroupService.createGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    // PUT /api/groups/{id}, ažuriraj grupu
    @PutMapping("/{id}")
    public ResponseEntity<CourseGroup> updateGroup(@PathVariable UUID id, @RequestBody CourseGroup group) {
        CourseGroup updated = courseGroupService.updateGroup(id, group);
        return ResponseEntity.ok(updated); // 200 OK
    }

    // DELETE /api/groups/{id}, obriši grupu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        courseGroupService.deleteGroup(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}