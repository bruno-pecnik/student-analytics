package com.fer.student_analytics;

import com.fer.student_analytics.model.*;
import com.fer.student_analytics.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component // spring upravlja ovom klasom automatski
public class DataSeeder implements CommandLineRunner {
    // CommandLineRunner znači da spring pozove run() metodu automatski kad se aplikacija pokrene

    // repositoryi za pristup bazi
    private final AppUserRepository userRepository;
    private final AcademicYearRepository academicYearRepository;
    private final CourseRepository courseRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final StudentRecordRepository studentRecordRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // objekt koji šifrira lozinke, hashirat ćemo ih

    public DataSeeder( // konstruktor
            AppUserRepository userRepository,
            AcademicYearRepository academicYearRepository,
            CourseRepository courseRepository,
            CourseGroupRepository courseGroupRepository,
            StudentEnrollmentRepository enrollmentRepository,
            GradeComponentRepository gradeComponentRepository,
            StudentRecordRepository studentRecordRepository) {
        this.userRepository = userRepository;
        this.academicYearRepository = academicYearRepository;
        this.courseRepository = courseRepository;
        this.courseGroupRepository = courseGroupRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeComponentRepository = gradeComponentRepository;
        this.studentRecordRepository = studentRecordRepository;
    }

    @Override
    public void run(String... args) {
        // ako već ima podataka, ne radi ništa
        if (academicYearRepository.count() > 0) {
            System.out.println("Baza već ima podatke, preskačem seed.");
            return;
        }

        System.out.println("Postavljam bazu s demo podacima ...");

        // kreiranje akademske godine
        AcademicYear godina = new AcademicYear();
        godina.setName("2025./2026.");
        godina.setStartDate(LocalDate.of(2025, 10, 1));
        godina.setEndDate(LocalDate.of(2026, 9, 30));
        academicYearRepository.save(godina);

        // kreiranje profesora
        SystemUser profesor = new SystemUser();
        profesor.setFirstName("Marko");
        profesor.setLastName("Markota");
        profesor.setEmail("marko@fer.hr");
        profesor.setPasswordHash(passwordEncoder.encode("profesor123"));
        profesor.setRole(SystemUser.Role.PROFESSOR);
        profesor.setAvatarUrl("");
        profesor.setCreatedAt(LocalDateTime.now());
        userRepository.save(profesor);

        // kreiranje admina
        SystemUser admin = new SystemUser();
        admin.setFirstName("Filip");
        admin.setLastName("Filipović");
        admin.setEmail("filip@fer.hr");
        admin.setPasswordHash(passwordEncoder.encode("filip123"));
        admin.setRole(SystemUser.Role.ADMIN);
        admin.setAvatarUrl("");
        admin.setCreatedAt(LocalDateTime.now());
        userRepository.save(admin);

        // kreiranje kolegija
        Course kolegij = new Course();
        kolegij.setName("Algoritmi i strukture podataka");
        kolegij.setCode("ASP");
        kolegij.setDescription("Osnove algoritama i struktura podataka");
        kolegij.setSemester(Course.Semester.WINTER);
        kolegij.setAcademicYear(godina);
        courseRepository.save(kolegij);

        Course kolegij2 = new Course();
        kolegij2.setName("Baze podataka");
        kolegij2.setCode("BP");
        kolegij2.setDescription("Osnove baza podataka");
        kolegij2.setSemester(Course.Semester.SUMMER);
        kolegij2.setAcademicYear(godina);
        courseRepository.save(kolegij2);

        // kreiranje grupa
        CourseGroup grupaA = new CourseGroup();
        grupaA.setName("Grupa A");
        grupaA.setCourse(kolegij);
        courseGroupRepository.save(grupaA);

        CourseGroup grupaB = new CourseGroup();
        grupaB.setName("Grupa B");
        grupaB.setCourse(kolegij);
        courseGroupRepository.save(grupaB);

        // kreiranje komponenti ocjenjivanja
        GradeComponent kolokvij = new GradeComponent();
        kolokvij.setName("Kolokvij 1");
        kolokvij.setMaxPoints(30.0f);
        kolokvij.setPassingThreshold(15.0f);
        kolokvij.setWeightPercent(30.0f);
        kolokvij.setIsRequired(true);
        kolokvij.setCourse(kolegij);
        gradeComponentRepository.save(kolokvij);

        GradeComponent ispit = new GradeComponent();
        ispit.setName("Završni ispit");
        ispit.setMaxPoints(50.0f);
        ispit.setPassingThreshold(25.0f);
        ispit.setWeightPercent(50.0f);
        ispit.setIsRequired(true);
        ispit.setCourse(kolegij);
        gradeComponentRepository.save(ispit);

        GradeComponent zadace = new GradeComponent();
        zadace.setName("Zadaće");
        zadace.setMaxPoints(20.0f);
        zadace.setPassingThreshold(10.0f);
        zadace.setWeightPercent(20.0f);
        zadace.setIsRequired(false);
        zadace.setCourse(kolegij);
        gradeComponentRepository.save(zadace);

        // kreiranje 10 studenata i upis u grupu A
        String[] imena = {"Ana", "Ivan", "Maja", "Luka", "Sara", "Petar", "Nina", "Tomislav", "Petra", "Matej"};
        String[] prezimena = {"Kovač", "Horvat", "Babić", "Marić", "Novak", "Jurić", "Perić", "Blažević", "Knežević", "Vuković"};

        for (int i = 0; i < 10; i++) {
            // kreiraj studenta
            SystemUser student = new SystemUser();
            student.setFirstName(imena[i]);
            student.setLastName(prezimena[i]);
            student.setEmail(imena[i].toLowerCase() + "@fer.hr");
            student.setPasswordHash(passwordEncoder.encode("student123"));
            student.setRole(SystemUser.Role.STUDENT);
            student.setAvatarUrl("");
            student.setCreatedAt(LocalDateTime.now());
            userRepository.save(student);

            // upiši studenta u grupu A
            StudentEnrollment upis = new StudentEnrollment();
            upis.setStudent(student);
            upis.setGroup(grupaA);
            enrollmentRepository.save(upis);

            // dodaj rezultate za kolokvij
            float bodoviKolokvij = 10.0f + i * 2; // bodovi od 10 do 28

            StudentRecord recordKolokvij = new StudentRecord();
            recordKolokvij.setEnrollment(upis);
            recordKolokvij.setComponent(kolokvij);
            recordKolokvij.setObligationMet(bodoviKolokvij >= 15.0f);
            recordKolokvij.setPoints(bodoviKolokvij);
            recordKolokvij.setRecordedAt(LocalDate.now());
            studentRecordRepository.save(recordKolokvij);

            // dodaj rezultate za zadaće
            StudentRecord recordZadace = new StudentRecord();
            recordZadace.setEnrollment(upis);
            recordZadace.setComponent(zadace);
            recordZadace.setPoints(8.0f + i); // bodovi od 8 do 17
            recordZadace.setObligationMet(i > 2); // prvih 3 nisu ispunili
            recordZadace.setRecordedAt(LocalDate.now());
            studentRecordRepository.save(recordZadace);
        }

        System.out.println("Punjenje baze demo podacima je završeno!");
    }
}