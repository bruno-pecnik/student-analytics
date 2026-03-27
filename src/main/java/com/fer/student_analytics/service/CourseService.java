package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.Course;
import com.fer.student_analytics.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository; // referenca na bazu

    public CourseService(CourseRepository courseRepository) { // konstruktor
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByAcademicYear(UUID academicYearId) {
        return courseRepository.findByAcademicYearId(academicYearId);
    }

    public Optional<Course> getCourseById(UUID id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) { // prvo normaliziram, pa validiram, pa spremam
        validateAcademicYear(course);
        validateSemester(course);
        course.setName(normalizeAndValidateName(course.getName()));
        course.setCode(normalizeAndValidateCode(course.getCode()));
        validateUniqueness(course.getCode(), course.getName(), 
            course.getAcademicYear().getId(), null);
        return courseRepository.save(course);
    }

    public Course updateCourse(UUID id, Course updatedCourse) {
        Course existingCourse = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kolegij sa ID: " + id + " nije pronađen."));
        validateAcademicYear(updatedCourse);
        validateSemester(updatedCourse);
        existingCourse.setName(normalizeAndValidateName(updatedCourse.getName()));
        existingCourse.setCode(normalizeAndValidateCode(updatedCourse.getCode()));
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setSemester(updatedCourse.getSemester());
        validateUniqueness(existingCourse.getCode(), existingCourse.getName(),
            existingCourse.getAcademicYear().getId(), id); // ignoriraj sebe kod updatea
        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kolegij sa ID: " + id + " nije pronađen.");
        }
        courseRepository.deleteById(id);
    }

    private String normalizeAndValidateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Naziv kolegija je obavezan."); // naziv je obavezan
        }
        name = name.trim(); // uklanjanje praznina
        if (name.isBlank()) {
            throw new IllegalArgumentException("Naziv kolegija je obavezan.");
        }
        return name;
    }

    private String normalizeAndValidateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Kod kolegija je obavezan."); // kod je obavezan
        }
        code = code.trim().toUpperCase(); // normaliziram kod na velika slova
        if (code.isBlank()) {
            throw new IllegalArgumentException("Kod kolegija je obavezan.");
        }
        return code;
    }

    private void validateAcademicYear(Course course) {
        // kolegij mora biti pridružen akademskoj godini
        if (course.getAcademicYear() == null || 
            course.getAcademicYear().getId() == null) {
            throw new IllegalArgumentException("Kolegij mora biti pridružen akademskoj godini.");
        }
    }

    private void validateSemester(Course course) {
        // semestar mora biti postavljen
        if (course.getSemester() == null) {
            throw new IllegalArgumentException("Semestar mora biti postavljen. (WINTER ili SUMMER)");
        }
    }

    private void validateUniqueness(String code, String name, UUID academicYearId, UUID currentCourseId) {

        // provjera duplikata po code-u
        if (courseRepository.existsByCodeAndAcademicYearId(code, academicYearId)) {

            boolean isSame = false;

            if (currentCourseId != null) {
                Optional<Course> existing = courseRepository.findById(currentCourseId);
                if (existing.isPresent()) {
                    isSame = existing.get().getCode().equals(code);
                }
            }

            if (!isSame) {
                throw new IllegalArgumentException(
                    "Kolegij s kodom '" + code + "' već postoji u ovoj akademskoj godini.");
            }
        }

        // provjera duplikata po nazivu
        if (courseRepository.existsByNameAndAcademicYearId(name, academicYearId)) {

            boolean isSame = false;

            if (currentCourseId != null) {
                Optional<Course> existing = courseRepository.findById(currentCourseId);
                if (existing.isPresent()) {
                    isSame = existing.get().getName().equals(name);
                }
            }

            if (!isSame) {
                throw new IllegalArgumentException(
                    "Kolegij s nazivom '" + name + "' već postoji u ovoj akademskoj godini.");
            }
        }
    }
}