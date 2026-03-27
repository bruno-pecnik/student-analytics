package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.StudentRecord;
import com.fer.student_analytics.repository.StudentRecordRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentRecordService {

    private final StudentRecordRepository studentRecordRepository; // referenca na bazu

    public StudentRecordService(StudentRecordRepository studentRecordRepository) { // konstruktor
        this.studentRecordRepository = studentRecordRepository;
    }

    public List<StudentRecord> getAllRecords() {
        return studentRecordRepository.findAll();
    }

    public List<StudentRecord> getRecordsByEnrollment(UUID enrollmentId) {
        return studentRecordRepository.findByEnrollmentId(enrollmentId);
    }

    public List<StudentRecord> getRecordsByComponent(UUID componentId) {
        return studentRecordRepository.findByComponentId(componentId);
    }

    public Optional<StudentRecord> getRecordById(UUID id) {
        return studentRecordRepository.findById(id);
    }

    public StudentRecord createRecord(StudentRecord record) { // prvo validiram, pa postavljam datum, pa spremam
        validateRecord(record);
        validatePoints(record);
        validateUniqueness(record, null);
        record.setRecordedAt(LocalDate.now()); // automatski postavljam datum unosa
        return studentRecordRepository.save(record);
    }

    public StudentRecord updateRecord(UUID id, StudentRecord updated) {
        StudentRecord existing = studentRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rezultat sa ID: " + id + " nije pronađen."));
    
        // prvo postavim nova polja na existing
        existing.setPoints(updated.getPoints());
        existing.setObligationMet(updated.getObligationMet());
        existing.setNote(updated.getNote());
    
        // validiram existing jer ima pravi component i enrollment
        // updated možda nema component pa bi provjera maxPoints bila nepouzdana
        validatePoints(existing);
        return studentRecordRepository.save(existing);
    }

    public void deleteRecord(UUID id) {
        if (!studentRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rezultat sa ID: " + id + " nije pronađen.");
        }
        studentRecordRepository.deleteById(id);
    }

    private void validateRecord(StudentRecord record) {
        // enrollment mora biti pridružen
        if (record.getEnrollment() == null || record.getEnrollment().getId() == null) {
            throw new IllegalArgumentException("Rezultat mora imati pridružen upis studenta.");
        }
        // komponenta mora biti pridružena
        if (record.getComponent() == null || record.getComponent().getId() == null) {
            throw new IllegalArgumentException("Rezultat mora imati pridruženu komponentu ocjenjivanja.");
        }
    }

    private void validatePoints(StudentRecord record) {
        // bodovi ne smiju biti negativni
        if (record.getPoints() != null && record.getPoints() < 0) {
            throw new IllegalArgumentException("Bodovi ne mogu biti negativni.");
        }
        // bodovi ne smiju prelaziti maksimalne bodove komponente
        if (record.getPoints() != null &&
            record.getComponent() != null &&
            record.getComponent().getMaxPoints() != null &&
            record.getPoints() > record.getComponent().getMaxPoints()) {
            throw new IllegalArgumentException(
                "Bodovi ne mogu prelaziti maksimalni broj bodova komponente (" +
                record.getComponent().getMaxPoints() + ").");
        }
    }

    private void validateUniqueness(StudentRecord record, UUID currentRecordId) {
        UUID enrollmentId = record.getEnrollment().getId();
        UUID componentId = record.getComponent().getId();

        // isti student ne može imati više zapisa za istu komponentu
        if (studentRecordRepository.existsByEnrollmentIdAndComponentId(enrollmentId, componentId)) {
            boolean isSame = false;
            if (currentRecordId != null) {
                Optional<StudentRecord> existing = studentRecordRepository.findById(currentRecordId);
                if (existing.isPresent()) {
                    isSame = existing.get().getEnrollment().getId().equals(enrollmentId) &&
                            existing.get().getComponent().getId().equals(componentId);
                }
            }
            if (!isSame) {
                throw new IllegalArgumentException("Rezultat za ovog studenta i ovu komponentu već postoji."
                );
            }
        }
    }
}