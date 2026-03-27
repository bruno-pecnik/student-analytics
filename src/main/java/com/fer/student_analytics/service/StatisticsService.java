package com.fer.student_analytics.service;

import com.fer.student_analytics.model.StudentRecord;
import com.fer.student_analytics.model.StudentEnrollment;
import com.fer.student_analytics.repository.StudentRecordRepository;
import com.fer.student_analytics.repository.StudentEnrollmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticsService { // OVU KLASU TREBAM NADOGRADITI 

    private final StudentRecordRepository studentRecordRepository;
    private final StudentEnrollmentRepository enrollmentRepository;

    public StatisticsService(
            StudentRecordRepository studentRecordRepository,
            StudentEnrollmentRepository enrollmentRepository) {
        this.studentRecordRepository = studentRecordRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // ukupni bodovi studenta na kolegiju
    public double getTotalPointsForEnrollment(UUID enrollmentId) {
        List<StudentRecord> records = studentRecordRepository.findByEnrollmentId(enrollmentId);
        double total = 0.0;
        for (StudentRecord r : records) {
            if (r.getPoints() != null) {
                total += r.getPoints();
            }
        }
        return total;
    }

    // prosjek bodova za sve studente u grupi
    public double getAveragePointsForGroup(UUID groupId) {
        List<StudentEnrollment> enrollments = enrollmentRepository.findByGroupId(groupId);
        if (enrollments.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (StudentEnrollment e : enrollments) {
            total += getTotalPointsForEnrollment(e.getId());
        }
        return total / enrollments.size();
    }

    // prosjek bodova za jednu komponentu
    public double getAveragePointsForComponent(UUID componentId) {
        List<StudentRecord> records = studentRecordRepository.findByComponentId(componentId);
        double total = 0.0;
        int count = 0;
        for (StudentRecord r : records) {
            if (r.getPoints() != null) {
                total += r.getPoints();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    // postotak studenata koji su ispunili obavezu za komponentu
    public double getObligationMetPercentage(UUID componentId) {
        List<StudentRecord> records = studentRecordRepository.findByComponentId(componentId);
        if (records.isEmpty()) {
            return 0.0;
        }
        int metCount = 0;
        for (StudentRecord r : records) {
            if (Boolean.TRUE.equals(r.getObligationMet())) {
                metCount++;
            }
        }
        return (double) metCount / records.size() * 100;
    }

    // provjeri može li student pristupiti ispitu
    public boolean canStudentAccessExam(UUID enrollmentId) {
        List<StudentRecord> records = studentRecordRepository.findByEnrollmentId(enrollmentId);
        for (StudentRecord r : records) {
            if (r.getComponent() != null &&
                Boolean.TRUE.equals(r.getComponent().getIsRequired())) {
                if (!Boolean.TRUE.equals(r.getObligationMet())) {
                    return false; // nije ispunio obavezu pa nema pristup ispitu
                }
            }
        }
        return true; // sve obavezne komponente su ispunjene
    }
}