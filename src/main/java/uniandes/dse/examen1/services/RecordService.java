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
        if (loginStudent == null && courseCode == null) {
            throw new InvalidRecordException ("El record debe tener un estudiante y un curso");
        }
        if (loginStudent !=null ){
            Optional<StudentEntity> student = studentRepository.findByLogin(loginStudent);
            if (student.isEmpty()){
                throw new EntityNotFoundException ("El estudiante no existe en la BD");

            }

            recordEntity.setStudent(student.get());
        }
        if (courseCode !=null ){
            Optional<CourseEntity> course = courseRepository.findByCourseCode(courseCode);
            if (course.isEmpty()){
                throw new EntityNotFoundException ("El curso no existe en la BD");

            }
            recordEntity.setCourse(course.get());
        }
        

        if (grade >= 1.5 && grade <= 5.0){
            recordEntity.setFinalGrade(grade);
        }
        else throw new InvalidRecordException ("la nota no es valida");
        
        

        return recordRepository.save(recordEntity);

    }
}
