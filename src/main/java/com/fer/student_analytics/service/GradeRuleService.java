package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.GradeRule;
import com.fer.student_analytics.repository.GradeRuleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GradeRuleService {

    private final GradeRuleRepository gradeRuleRepository; // referenca na bazu

    public GradeRuleService(GradeRuleRepository gradeRuleRepository) { // konstruktor
        this.gradeRuleRepository = gradeRuleRepository;
    }

    public List<GradeRule> getAllGradeRules() {
        return gradeRuleRepository.findAll();
    }

    public List<GradeRule> getGradeRulesByCourse(UUID courseId) {
        return gradeRuleRepository.findByCourseId(courseId);
    }

    public Optional<GradeRule> getGradeRuleById(UUID id) {
        return gradeRuleRepository.findById(id);
    }

    public GradeRule createGradeRule(GradeRule gradeRule) { // prvo validiram, pa spremam
        validateCoursePresent(gradeRule);
        validateGrade(gradeRule);
        validatePoints(gradeRule);
        validateUniqueness(gradeRule.getGrade(), gradeRule.getCourse().getId(), null);
        return gradeRuleRepository.save(gradeRule);
    }

    public GradeRule updateGradeRule(UUID id, GradeRule updatedGradeRule) {
        GradeRule existingGradeRule = gradeRuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pravilo ocjenjivanja sa ID: " + id + " nije pronađeno."));
        validateCoursePresent(updatedGradeRule);
        validateGrade(updatedGradeRule);
        validatePoints(updatedGradeRule);
        validateUniqueness(updatedGradeRule.getGrade(), updatedGradeRule.getCourse().getId(), id); // ignoriraj sebe kod updatea
        existingGradeRule.setGrade(updatedGradeRule.getGrade());
        existingGradeRule.setMinPoints(updatedGradeRule.getMinPoints());
        existingGradeRule.setMaxPoints(updatedGradeRule.getMaxPoints());
        return gradeRuleRepository.save(existingGradeRule);
    }

    public void deleteGradeRule(UUID id) {
        if (!gradeRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pravilo ocjenjivanja sa ID: " + id + " nije pronađeno.");
        }
        gradeRuleRepository.deleteById(id);
    }

    private void validateCoursePresent(GradeRule gradeRule) {
        // pravilo mora biti pridruženo kolegiju
        if (gradeRule.getCourse() == null || gradeRule.getCourse().getId() == null) {
            throw new IllegalArgumentException("Pravilo ocjenjivanja mora biti pridruženo kolegiju.");
        }
    }

    private void validateGrade(GradeRule gradeRule) {
        // ocjena je obavezna i mora biti između 1 i 5
        if (gradeRule.getGrade() == null) {
            throw new IllegalArgumentException("Ocjena je obavezna.");
        }
        if (gradeRule.getGrade() < 1 || gradeRule.getGrade() > 5) {
            throw new IllegalArgumentException("Ocjena mora biti između 1 i 5.");
        }
    }

    private void validatePoints(GradeRule gradeRule) { // gettere generira lombok*
        // minPoints i maxPoints ne smiju biti negativni
        if (gradeRule.getMinPoints() != null && gradeRule.getMinPoints() < 0) {
            throw new IllegalArgumentException("Minimalni bodovi ne mogu biti negativni.");
        }
        if (gradeRule.getMaxPoints() != null && gradeRule.getMaxPoints() < 0) {
            throw new IllegalArgumentException("Maksimalni bodovi ne mogu biti negativni.");
        }
        // minPoints mora biti manji od maxPoints
        if (gradeRule.getMinPoints() != null &&
            gradeRule.getMaxPoints() != null &&
            gradeRule.getMinPoints() >= gradeRule.getMaxPoints()) {
            throw new IllegalArgumentException("Minimalni bodovi moraju biti manji od maksimalnih.");
        }
    }

    private void validateUniqueness(Integer grade, UUID courseId, UUID currentRuleId) {
        // ista ocjena ne smije postojati više puta unutar istog kolegija
        if (gradeRuleRepository.existsByGradeAndCourseId(grade, courseId)) {
            boolean isSame = false;
            if (currentRuleId != null) {
                Optional<GradeRule> existing = gradeRuleRepository.findById(currentRuleId);
                if (existing.isPresent()) {
                    isSame = existing.get().getGrade().equals(grade);
                }
            }
            if (!isSame) {
                throw new IllegalArgumentException("Pravilo za ocjenu " + grade + " već postoji za ovaj kolegij."
                );
            }
        }
    }
}