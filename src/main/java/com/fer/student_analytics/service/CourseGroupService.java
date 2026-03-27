package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.CourseGroup;
import com.fer.student_analytics.repository.CourseGroupRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseGroupService {

    private final CourseGroupRepository courseGroupRepository; // referenca na bazu

    public CourseGroupService(CourseGroupRepository courseGroupRepository) { // konstruktor
        this.courseGroupRepository = courseGroupRepository;
    }

    public List<CourseGroup> getAllGroups() {
        return courseGroupRepository.findAll();
    }

    public List<CourseGroup> getGroupsByCourse(UUID courseId) {
        return courseGroupRepository.findByCourseId(courseId);
    }

    public Optional<CourseGroup> getGroupById(UUID id) {
        return courseGroupRepository.findById(id);
    }

    public CourseGroup createGroup(CourseGroup group) { // prvo normaliziram, pa validiram, pa spremam
        validateCoursePresent(group); // kolegij mora biti pridružen 
        group.setName(normalizeAndValidateGroupName(group.getName()));
        validateGroupUniqueness(group.getName(), group.getCourse().getId(), null); // provjeri duplikat
        return courseGroupRepository.save(group);
    }

    public CourseGroup updateGroup(UUID id, CourseGroup updated) {
        CourseGroup existing = courseGroupRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grupa sa ID: " + id + " nije pronađena."));
        validateCoursePresent(updated); // kolegij mora biti pridružen
        existing.setName(normalizeAndValidateGroupName(updated.getName()));
        validateGroupUniqueness(existing.getName(), existing.getCourse().getId(), id); // provjeri duplikat, ali ne za sebe
        return courseGroupRepository.save(existing);
    }

    public void deleteGroup(UUID id) {
        if (!courseGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grupa sa ID: " + id + " nije pronađena.");
        }
        courseGroupRepository.deleteById(id);
    }

    private String normalizeAndValidateGroupName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Naziv grupe je obavezan."); // naziv je obavezan
        }

        name = name.trim(); // uklanjanje praznina prije i poslije

        if (name.isBlank()) {
            throw new IllegalArgumentException("Naziv grupe je obavezan."); // naziv ne smije biti prazan
        }

        return name;
    }

    private void validateCoursePresent(CourseGroup group) {
        if (group.getCourse() == null || group.getCourse().getId() == null) {
            throw new IllegalArgumentException("Grupa mora imati pridružen kolegij."); // svaka grupa pripada jednom kolegiju
        }
    }

    private void validateGroupUniqueness(String name, UUID courseId, UUID currentGroupId) {
        // provjeri postoji li već grupa s istim imenom na istom kolegiju
        List<CourseGroup> existing = courseGroupRepository.findByCourseId(courseId);
        boolean duplicate = existing.stream()
            .filter(g -> currentGroupId == null || !g.getId().equals(currentGroupId)) // ignoriraj sebe kod updatea
            .anyMatch(g -> g.getName().equalsIgnoreCase(name)); // case insensitive usporedba

        if (duplicate) {
            throw new IllegalArgumentException("Grupa s nazivom '" + name + "' već postoji za ovaj kolegij.");
        }
    }
}