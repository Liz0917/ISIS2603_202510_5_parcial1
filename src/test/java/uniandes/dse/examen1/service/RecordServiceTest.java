package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.services.RecordService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    private PodamFactory factory = new PodamFactoryImpl();

    private String login;
    private String courseCode;

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
        CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();

        StudentEntity newStudent = factory.manufacturePojo(StudentEntity.class);
        newStudent = studentService.createStudent(newStudent);
        login = newStudent.getLogin();
    }

    /**
     * Tests the normal creation of a record for a student in a course
     */
    @Test
    void testCreateRecord() throws InvalidRecordException {
        RecordEntity record = factory.manufacturePojo(RecordEntity.class);
        RecordEntity createdRecord = recordService.createRecord(record, login, courseCode, 4.0, "2024-1");
        assertNotNull(createdRecord);
        assertEquals(4.0, createdRecord.getFinalGrade());
    }

    /**
     * Tests the creation of a record when the login of the student is wrong
     */
    @Test
    void testCreateRecordMissingStudent() {
        RecordEntity record = factory.manufacturePojo(RecordEntity.class);
        Exception exception = assertThrows(Exception.class, () -> 
            recordService.createRecord(record, "wrongLogin", courseCode, 4.0, "2024-1"));
        assertTrue(exception instanceof InvalidRecordException || exception instanceof jakarta.persistence.EntityNotFoundException);
    }

    /**
     * Tests the creation of a record when the course code is wrong
     */
    @Test
    void testCreateInscripcionMissingCourse() {
        RecordEntity record = factory.manufacturePojo(RecordEntity.class);
        Exception exception = assertThrows(Exception.class, () -> 
            recordService.createRecord(record, login, "wrongCode", 4.0, "2024-1"));
        assertTrue(exception instanceof InvalidRecordException || exception instanceof jakarta.persistence.EntityNotFoundException);
    }

    /**
     * Tests the creation of a record when the grade is not valid
     */
    @Test
    void testCreateInscripcionWrongGrade() {
        RecordEntity record = factory.manufacturePojo(RecordEntity.class);
        Exception exception = assertThrows(InvalidRecordException.class, () -> 
            recordService.createRecord(record, login, courseCode, 1.0, "2024-1"));
        assertEquals("La nota no es válida. Debe estar entre 1.5 y 5.0", exception.getMessage());
    }

    /**
     * Tests the creation of a record when the student already has a passing grade
     * for the course
     */
    @Test
    void testCreateInscripcionRepetida1() throws InvalidRecordException {
        RecordEntity record1 = factory.manufacturePojo(RecordEntity.class);
        recordService.createRecord(record1, login, courseCode, 3.5, "2024-1");
        RecordEntity record2 = factory.manufacturePojo(RecordEntity.class);
        Exception exception = assertThrows(InvalidRecordException.class, () -> 
            recordService.createRecord(record2, login, courseCode, 3.0, "2024-2"));
        assertEquals("El estudiante ya aprobó este curso y no puede volver a verlo", exception.getMessage());
    }

    /**
     * Tests the creation of a record when the student already has a record for the
     * course, but he has not passed the course yet.
     */
    @Test
    void testCreateInscripcionRepetida2() throws InvalidRecordException {
        RecordEntity record1 = factory.manufacturePojo(RecordEntity.class);
        recordService.createRecord(record1, login, courseCode, 2.5, "2024-1");
        RecordEntity record2 = factory.manufacturePojo(RecordEntity.class);
        assertDoesNotThrow(() -> recordService.createRecord(record2, login, courseCode, 2.0, "2024-2"));
    }
}
