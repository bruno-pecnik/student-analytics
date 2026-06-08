package com.fer.student_analytics;

import com.fer.student_analytics.model.*;
import com.fer.student_analytics.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final AcademicYearRepository academicYearRepository;
    private final CourseRepository courseRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final StudentRecordRepository studentRecordRepository;
    private final GradeRuleRepository gradeRuleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeeder(
            AppUserRepository userRepository,
            AcademicYearRepository academicYearRepository,
            CourseRepository courseRepository,
            CourseGroupRepository courseGroupRepository,
            StudentEnrollmentRepository enrollmentRepository,
            GradeComponentRepository gradeComponentRepository,
            StudentRecordRepository studentRecordRepository,
            GradeRuleRepository gradeRuleRepository) {
        this.userRepository = userRepository;
        this.academicYearRepository = academicYearRepository;
        this.courseRepository = courseRepository;
        this.courseGroupRepository = courseGroupRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeComponentRepository = gradeComponentRepository;
        this.studentRecordRepository = studentRecordRepository;
        this.gradeRuleRepository = gradeRuleRepository;
    }

    @Override
    public void run(String... args) {
        if (academicYearRepository.count() > 0) {
            System.out.println("Baza već ima podatke, preskačem seed.");
            return;
        }

        System.out.println("Postavljam bazu s demo podacima...");

        // admin
        SystemUser admin = new SystemUser();
        admin.setFirstName("Filip");
        admin.setLastName("Filipović");
        admin.setEmail("filip@fer.hr");
        admin.setPasswordHash(passwordEncoder.encode("filip123"));
        admin.setRole(SystemUser.Role.ADMIN);
        admin.setAvatarUrl("");
        admin.setCreatedAt(LocalDateTime.now());
        userRepository.save(admin);

        // profesor 1
        SystemUser profesor1 = new SystemUser();
        profesor1.setFirstName("Marko");
        profesor1.setLastName("Markota");
        profesor1.setEmail("marko@fer.hr");
        profesor1.setPasswordHash(passwordEncoder.encode("profesor123"));
        profesor1.setRole(SystemUser.Role.PROFESSOR);
        profesor1.setAvatarUrl("");
        profesor1.setCreatedAt(LocalDateTime.now());
        userRepository.save(profesor1);

        // profesor 2
        SystemUser profesor2 = new SystemUser();
        profesor2.setFirstName("Iva");
        profesor2.setLastName("Ivić");
        profesor2.setEmail("iva@fer.hr");
        profesor2.setPasswordHash(passwordEncoder.encode("profesor123"));
        profesor2.setRole(SystemUser.Role.PROFESSOR);
        profesor2.setAvatarUrl("");
        profesor2.setCreatedAt(LocalDateTime.now());
        userRepository.save(profesor2);

        String[] imena = {
            "Ana", "Ivan", "Maja", "Luka", "Sara", "Petar", "Nina", "Tomislav", "Petra", "Matej",
            "Ema", "Josip", "Lucija", "Ante", "Mia", "Karlo", "Tea", "Domagoj", "Klara", "Bruno",
            "Lea", "Nikola", "Iva", "Marko", "Zara", "Filip", "Dora", "Leon", "Marta", "Roko"
        };
        
        String[] prezimena = {
            "Kovač", "Horvat", "Pećnik", "Marić", "Novak",
            "Jurić", "Perić", "Blažević", "Knežević", "Vuković",
            "Pavić", "Matić", "Tomić", "Petrović", "Lovrić",
            "Šimić", "Radić", "Brkić", "Vidović", "Grgić",
            "Bošnjak", "Kralj", "Barić", "Lukić", "Vukelić",
            "Đurić", "Mandić", "Klarić", "Pavlović", "Markota"
        };

        // kolegiji
        String[] naziviKolegija = {
            "Matematička analiza 1",
            "Uvod u programiranje",
            "Linearna algebra",
            "Vještine komuniciranja",
            "Digitalna logika",
            "Matematička analiza 2",
            "Objektno orijentirano programiranje",
            "Menadžment u inženjerstvu",
            "Fizika 1",
            "Osnove elektrotehnike"
        };
        String[] kodoviKolegija = {
            "MA1", "UP", "LA", "VK", "DL", "MA2", "OOP", "MUI", "FIZ1", "OE"
        };
        Course.Semester[] semestri = {
            Course.Semester.WINTER, Course.Semester.WINTER, Course.Semester.WINTER,
            Course.Semester.WINTER, Course.Semester.WINTER,
            Course.Semester.SUMMER, Course.Semester.SUMMER, Course.Semester.SUMMER,
            Course.Semester.SUMMER, Course.Semester.SUMMER
        };

        // bodovi rastu kroz godine za vidljiv trend
        float[] faktorPoGodini = {
            0.60f, 0.63f, 0.66f, 0.69f, 0.72f,
            0.75f, 0.78f, 0.81f, 0.84f, 0.88f
        };

        for (int g = 0; g < 10; g++) {
            int godinaPocetak = 2015 + g;
            String nazivGodine = godinaPocetak + "./" + (godinaPocetak + 1) + ".";

            // kreiraj akademsku godinu
            AcademicYear godina = new AcademicYear();
            godina.setName(nazivGodine);
            godina.setStartDate(LocalDate.of(godinaPocetak, 10, 1));
            godina.setEndDate(LocalDate.of(godinaPocetak + 1, 9, 30));
            academicYearRepository.save(godina);

            // kreiraj 30 studenata za ovu godinu
            List<SystemUser> studentiGodine = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                SystemUser student = new SystemUser();
                student.setFirstName(imena[i]);
                student.setLastName(prezimena[(i + g) % 30]); 
                student.setEmail(imena[i].toLowerCase() + "." + prezimena[(i + g) % 30].toLowerCase() + "." + godinaPocetak + "@fer.hr");
                student.setPasswordHash(passwordEncoder.encode("student123"));
                student.setRole(SystemUser.Role.STUDENT);
                student.setAvatarUrl("");
                student.setCreatedAt(LocalDateTime.now());
                userRepository.save(student);
                studentiGodine.add(student);
            }

            // kreiraj kolegije za ovu godinu
            for (int k = 0; k < 10; k++) {
                Course kolegij = new Course();
                kolegij.setName(naziviKolegija[k]);
                kolegij.setCode(kodoviKolegija[k]);
                kolegij.setDescription(naziviKolegija[k]);
                kolegij.setSemester(semestri[k]);
                kolegij.setAcademicYear(godina);
                courseRepository.save(kolegij);

                // grade rules
                kreirajGradeRules(kolegij);

                // komponente
                GradeComponent kolokvij = kreirajKomponentu("Kolokvij 1", 30.0f, 15.0f, 30.0f, true, kolegij);
                GradeComponent ispit = kreirajKomponentu("Završni ispit", 50.0f, 25.0f, 50.0f, true, kolegij);
                GradeComponent zadace = kreirajKomponentu("Zadaće", 20.0f, 10.0f, 20.0f, false, kolegij);

                // grupe
                CourseGroup grupaA = kreirajGrupu("Grupa A", kolegij);
                CourseGroup grupaB = kreirajGrupu("Grupa B", kolegij);

                // studenti 0-14 u grupu A, 15-29 u grupu B
                for (int i = 0; i < 30; i++) {
                    SystemUser student = studentiGodine.get(i);
                    CourseGroup grupa = i < 15 ? grupaA : grupaB;

                    StudentEnrollment upis = kreirajUpis(student, grupa);

                    java.util.Random rand = new java.util.Random(g * 1000L + k * 100L + i);

                    int bodoviKolokvij = rand.nextInt(31); // 0-30
                    int bodoviIspit = rand.nextInt(51);     // 0-50
                    int bodoviZadace = rand.nextInt(21);    // 0-20

                    if (rand.nextFloat() < 0.6f) {
                        bodoviKolokvij = Math.min(bodoviKolokvij + rand.nextInt(10), 30);
                        bodoviIspit = Math.min(bodoviIspit + rand.nextInt(15), 50);
                        bodoviZadace = Math.min(bodoviZadace + rand.nextInt(8), 20);
                    }

                    kreirajRezultat(upis, kolokvij, bodoviKolokvij, LocalDate.of(godinaPocetak + 1, 1, 15));
                    kreirajRezultat(upis, ispit, bodoviIspit, LocalDate.of(godinaPocetak + 1, 2, 1));
                    kreirajRezultat(upis, zadace, bodoviZadace, LocalDate.of(godinaPocetak + 1, 1, 1));
                }
            }
        }

        System.out.println("Punjenje baze demo podacima je završeno!");
    }

    private GradeComponent kreirajKomponentu(String naziv, float max, float prag, float tezina, boolean obavezna, Course kolegij) {
        GradeComponent k = new GradeComponent();
        k.setName(naziv);
        k.setMaxPoints(max);
        k.setPassingThreshold(prag);
        k.setWeightPercent(tezina);
        k.setIsRequired(obavezna);
        k.setCourse(kolegij);
        return gradeComponentRepository.save(k);
    }

    private CourseGroup kreirajGrupu(String naziv, Course kolegij) {
        CourseGroup g = new CourseGroup();
        g.setName(naziv);
        g.setCourse(kolegij);
        return courseGroupRepository.save(g);
    }

    private StudentEnrollment kreirajUpis(SystemUser student, CourseGroup grupa) {
        StudentEnrollment u = new StudentEnrollment();
        u.setStudent(student);
        u.setGroup(grupa);
        return enrollmentRepository.save(u);
    }

    private void kreirajRezultat(StudentEnrollment upis, GradeComponent komponenta, int bodovi, LocalDate datum) {
        float stvarniBodovi = Math.min(bodovi, komponenta.getMaxPoints() - 1);
        StudentRecord r = new StudentRecord();
        r.setEnrollment(upis);
        r.setComponent(komponenta);
        r.setPoints(stvarniBodovi);
        r.setObligationMet(stvarniBodovi >= komponenta.getPassingThreshold());
        r.setRecordedAt(datum);
        studentRecordRepository.save(r);
    }

    private void kreirajGradeRules(Course kolegij) {
        float[][] pravila = {
            {0, 49, 1},
            {50, 62, 2},
            {63, 75, 3},
            {76, 88, 4},
            {89, 100, 5}
        };
        for (float[] p : pravila) {
            GradeRule rule = new GradeRule();
            rule.setCourse(kolegij);
            rule.setMinPoints(p[0]);
            rule.setMaxPoints(p[1]);
            rule.setGrade(Math.round(p[2]));
            gradeRuleRepository.save(rule);
        }
    }
}