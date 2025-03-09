package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.services.CourseService;

@DataJpaTest
@Transactional
@Import(CourseService.class)
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private CourseEntity existingCourse;

    @BeforeEach
    void setUp() {
        existingCourse = factory.manufacturePojo(CourseEntity.class);
        entityManager.persist(existingCourse);
    }

    @Test
    void testCreateRecordMissingCourse() {
        // TODO
        CourseEntity course = factory.manufacturePojo(CourseEntity.class);
        course.setCourseCode(null);
        assertThrows(IllegalArgumentException.class, ()->{
            courseService.createCourse(course);
        });

    }

    @Test
    void testCreateRepeatedCourse() {
        CourseEntity duplicateCourse = factory.manufacturePojo(CourseEntity.class);
        duplicateCourse.setCourseCode(existingCourse.getCourseCode());

        assertThrows(RepeatedCourseException.class, () -> {
            courseService.createCourse(duplicateCourse);
        });
}
}
