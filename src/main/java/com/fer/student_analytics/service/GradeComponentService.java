package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.GradeComponent;
import com.fer.student_analytics.repository.GradeComponentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GradeComponentService {

    private final GradeComponentRepository gradeComponentRepository; // referenca na bazu

    public GradeComponentService(GradeComponentRepository gradeComponentRepository) { // konstruktor
        this.gradeComponentRepository = gradeComponentRepository;
    }

    public List<GradeComponent> getAllComponents() {
        return gradeComponentRepository.findAll();
    }

    public List<GradeComponent> getComponentsByCourse(UUID courseId) {
        return gradeComponentRepository.findByCourseId(courseId);
    }

    public Optional<GradeComponent> getComponentById(UUID id) {
        return gradeComponentRepository.findById(id);
    }

    public GradeComponent createComponent(GradeComponent component) { // prvo normaliziram, pa validiram, pa spremam
        validateCoursePresent(component);
        component.setName(normalizeAndValidateName(component.getName()));
        validatePoints(component);
        validateWeightPercent(component);
        validateUniqueness(component.getName(), component.getCourse().getId(), null);
        return gradeComponentRepository.save(component);
    }

    public GradeComponent updateComponent(UUID id, GradeComponent updatedComponent) {
        GradeComponent existingComponent = gradeComponentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Komponenta sa ID: " + id + " nije pronađena."));
        validateCoursePresent(updatedComponent);
        existingComponent.setName(normalizeAndValidateName(updatedComponent.getName()));
        validatePoints(updatedComponent);
        validateWeightPercent(updatedComponent);
        validateUniqueness(updatedComponent.getName(), updatedComponent.getCourse().getId(), id); // ignoriraj sebe kod updatea
        existingComponent.setWeightPercent(updatedComponent.getWeightPercent());
        existingComponent.setMaxPoints(updatedComponent.getMaxPoints());
        existingComponent.setPassingThreshold(updatedComponent.getPassingThreshold());
        existingComponent.setIsRequired(updatedComponent.getIsRequired());
        return gradeComponentRepository.save(existingComponent);
    }

    public void deleteComponent(UUID id) {
        if (!gradeComponentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Komponenta sa ID: " + id + " nije pronađena.");
        }
        gradeComponentRepository.deleteById(id);
    }

    private String normalizeAndValidateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Naziv komponente je obavezan."); // naziv je obavezan
        }
        name = name.trim(); // uklanjanje praznina
        if (name.isBlank()) {
            throw new IllegalArgumentException("Naziv komponente je obavezan.");
        }
        return name;
    }

    private void validateCoursePresent(GradeComponent component) {
        // komponenta mora biti pridružena kolegiju
        if (component.getCourse() == null || component.getCourse().getId() == null) {
            throw new IllegalArgumentException("Komponenta mora biti pridružena kolegiju.");
        }
    }

    private void validatePoints(GradeComponent component) {
        // maxPoints mora biti veći od 0
        if (component.getMaxPoints() == null || component.getMaxPoints() <= 0) { // getMaxPoints dolazi iz lomboka*
            throw new IllegalArgumentException("Maksimalni broj bodova mora biti veći od 0.");
        }
        // passingThreshold ne smije biti negativan niti prelaziti maxPoints
        if (component.getPassingThreshold() != null) { // getPassingThreshold generira lombok*
            if (component.getPassingThreshold() < 0) {
                throw new IllegalArgumentException("Prag prolaza ne može biti negativan.");
            }
            if (component.getPassingThreshold() > component.getMaxPoints()) {
                throw new IllegalArgumentException("Prag prolaza ne može biti veći od maksimalnih bodova.");
            }
        }
    }

    private void validateWeightPercent(GradeComponent component) {
        // weightPercent mora biti između 0 i 100 ako je postavljen
        if (component.getWeightPercent() != null &&
            (component.getWeightPercent() < 0 || component.getWeightPercent() > 100)) {
            throw new IllegalArgumentException("Težina komponente mora biti između 0 i 100.");
        }
    }

    private void validateUniqueness(String name, UUID courseId, UUID currentComponentId) {
        // ista komponenta ne smije postojati više puta u istom kolegiju
        if (gradeComponentRepository.existsByNameAndCourseId(name, courseId)) {
            boolean isSame = false;
            if (currentComponentId != null) {
                Optional<GradeComponent> existing = gradeComponentRepository.findById(currentComponentId);
                if (existing.isPresent()) {
                    isSame = existing.get().getName().equals(name);
                }
            }
            if (!isSame) {
                throw new IllegalArgumentException("Komponenta s nazivom '" + name + "' već postoji za ovaj kolegij."
                );
            }
        }
    }
}