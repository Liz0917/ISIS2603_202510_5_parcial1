package uniandes.dse.examen1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class RecordService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    RecordRepository recordRepository;

    
    public RecordEntity createRecord( RecordEntity recordEntity , String loginStudent, String courseCode, Double grade, String semester)
            throws InvalidRecordException {
        // TODO
        if (loginStudent == null || courseCode == null) {
            throw new InvalidRecordException("El record debe tener un estudiante y un curso");
        }

        Optional<StudentEntity> studentOpt = studentRepository.findByLogin(loginStudent);
        if (studentOpt.isEmpty()) {
            throw new EntityNotFoundException("El estudiante no existe en la BD");
        }
        StudentEntity student = studentOpt.get();

        Optional<CourseEntity> courseOpt = courseRepository.findByCourseCode(courseCode);
        if (courseOpt.isEmpty()) {
            throw new EntityNotFoundException("El curso no existe en la BD");
        }
        CourseEntity course = courseOpt.get();

        if (grade < 1.5 || grade > 5.0) {
            throw new InvalidRecordException("La nota no es válida. Debe estar entre 1.5 y 5.0");
        }

        for (RecordEntity record : student.getRecords()) {
            if (record.getCourse().getId().equals(course.getId()) && record.getFinalGrade() >= 3.0) {
                throw new InvalidRecordException("El estudiante ya aprobó este curso y no puede volver a verlo");
            }
        }

        recordEntity.setStudent(student);
        recordEntity.setCourse(course);
        recordEntity.setFinalGrade(grade);
        recordEntity.setSemester(semester);

        RecordEntity savedRecord = recordRepository.save(recordEntity);

        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
        }
        if (!course.getStudents().contains(student)) {
            course.getStudents().add(student);
        }

        studentRepository.save(student);
        courseRepository.save(course);

        return savedRecord;
    }
}
