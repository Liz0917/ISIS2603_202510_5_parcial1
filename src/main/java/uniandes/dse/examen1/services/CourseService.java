package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.repositories.CourseRepository;

@Slf4j
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    public CourseEntity createCourse(CourseEntity newCourse) throws RepeatedCourseException {
        // TODO
        

        Optional<CourseEntity> existingCourse = courseRepository.findByCourseCode(newCourse.getCourseCode());
        if (existingCourse.isPresent()) {
            throw new RepeatedCourseException("Ya existe un curso con ese código");
        }

        return courseRepository.save(newCourse);
    }
}
