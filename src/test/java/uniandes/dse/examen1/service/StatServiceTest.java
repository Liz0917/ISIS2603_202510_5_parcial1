package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.RecordRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.RecordService;
import uniandes.dse.examen1.services.StatsService;
import uniandes.dse.examen1.services.StudentService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class StatServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;
    @Autowired 
    private StatsService statsService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private RecordRepository recordRepository;

    private StudentEntity student;
    private CourseEntity course;

    private PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        student = factory.manufacturePojo(StudentEntity.class);
        student.setLogin("student1");
        student = studentRepository.save(student);

        course = factory.manufacturePojo(CourseEntity.class);
        course.setCourseCode("COURSE123");
        course = courseRepository.save(course);

    }
    @Test
    void testCalculateStudentAverage_NoRecords() {
        assertEquals(0.0, statsService.calculateStudentAverage("student1"));
    }

    @Test
    void testCalculateStudentAverage_WithRecords() {
        RecordEntity record1 = factory.manufacturePojo(RecordEntity.class);
        record1.setStudent(student);
        record1.setFinalGrade(4.0);
        recordRepository.save(record1);

        RecordEntity record2 = factory.manufacturePojo(RecordEntity.class);
        record2.setStudent(student);
        record2.setFinalGrade(3.0);
        recordRepository.save(record2);

        assertEquals(3.5, statsService.calculateStudentAverage("student1"), 0.01);
    }

    @Test
    void testCalculateCourseAverage_NoRecords() {
        assertEquals(0.0, statsService.calculateCourseAverage("COURSE123"));
    }

    @Test
    void testCalculateCourseAverage_WithRecords() {
        StudentEntity student2 = factory.manufacturePojo(StudentEntity.class);
        student2.setLogin("student2");
        student2 = studentRepository.save(student2);

        RecordEntity record1 = factory.manufacturePojo(RecordEntity.class);
        record1.setCourse(course);
        record1.setStudent(student);
        record1.setFinalGrade(4.5);
        recordRepository.save(record1);

        RecordEntity record2 = factory.manufacturePojo(RecordEntity.class);
        record2.setCourse(course);
        record2.setStudent(student2);
        record2.setFinalGrade(3.5);
        recordRepository.save(record2);

        assertEquals(4.0, statsService.calculateCourseAverage("COURSE123"), 0.01);
    }

    @Test
    void testCalculateStudentAverage_InvalidStudent() {
        assertThrows(IllegalArgumentException.class, () -> statsService.calculateStudentAverage("invalid_student"));
    }

    @Test
    void testCalculateCourseAverage_InvalidCourse() {
        assertThrows(IllegalArgumentException.class, () -> statsService.calculateCourseAverage("INVALID_COURSE"));
    }

    @Test
    void testFailure() {
        // TODO
        fail("always fails ...");
    }
}
