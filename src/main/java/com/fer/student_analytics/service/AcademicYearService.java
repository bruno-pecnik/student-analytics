package com.fer.student_analytics.service;

import com.fer.student_analytics.exception.ResourceNotFoundException;
import com.fer.student_analytics.model.AcademicYear;
import com.fer.student_analytics.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service 
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository; // referenca na repozitorij, preko njega zovem bazu

    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }
    // mogao sam koristit lombok za generiranje konstruktora, ali bi ispalo više koda nego da ja napišem konstruktor
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    public Optional<AcademicYear> getAcademicYearById(UUID id) {
        return academicYearRepository.findById(id);
    }

    public AcademicYear createAcademicYear(AcademicYear academicYear) {
        validateDates(academicYear);
        return academicYearRepository.save(academicYear);
    }

    public AcademicYear updateAcademicYear(UUID id, AcademicYear updatedAcademicYear) { // update godine po ID-u
        // updatedAcademicYear mi je privremeni objekt, ne postoji u bazi već njega kopiram u stvarnu godinu
        AcademicYear existingAcademicYear = academicYearRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Akademska godina sa ID: " + id + " nije pronadena."));
        validateDates(updatedAcademicYear); // baci error ako ne valjaju datumi
        existingAcademicYear.setName(updatedAcademicYear.getName());
        existingAcademicYear.setStartDate(updatedAcademicYear.getStartDate());
        existingAcademicYear.setEndDate(updatedAcademicYear.getEndDate());
        // ID se ne mijenja, njega generira sustav, tako da mozemo pratiti tu istu godinu
        // ime je dobro moći mijenjati radi tipfelera, a datumi se mogu mijenjati po potrebi
        return academicYearRepository.save(existingAcademicYear);
    }

    public void deleteAcademicYear(UUID id) {
        if (!academicYearRepository.existsById(id)) {
            throw new ResourceNotFoundException("Akademska godina sa ID: " + id + " nije pronadena.");
        }
        academicYearRepository.deleteById(id);
    }

    /* private void validateDates(AcademicYear academicYear) {
        if (academicYear.getStartDate() != null && academicYear.getEndDate() != null &&
            !academicYear.getStartDate().isBefore(academicYear.getEndDate())) {
            throw new IllegalArgumentException("Datum pocetka ne smije biti nakon datuma zavrsetka akademske godine.");
        }
    }
    */ // ovo je moj prijasnji kod za validateDates, novi je bolji jer se lakse cita
    
    private void validateDates(AcademicYear academicYear) {
        LocalDate start = academicYear.getStartDate();
        LocalDate end = academicYear.getEndDate();

        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException("Datum pocetka ne smije biti nakon datuma zavrsetka akademske godine.");
        }
    }
}