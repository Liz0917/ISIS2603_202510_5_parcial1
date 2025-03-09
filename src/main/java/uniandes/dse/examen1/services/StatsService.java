package uniandes.dse.examen1.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class StatsService {

    @Autowired
    StudentRepository estudianteRepository;

    @Autowired
    CourseRepository cursoRepository;

    @Autowired
    RecordRepository inscripcionRepository;

    public Double calculateStudentAverage(String login) {
        // TODO
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("El login del estudiante no puede ser nulo o vacío.");
        }

        Optional<StudentEntity> estudianteOpt = estudianteRepository.findByLogin(login);
        if (estudianteOpt.isEmpty()) {
            throw new IllegalArgumentException("El estudiante no existe.");
        }

        StudentEntity estudiante = estudianteOpt.get();
        List<RecordEntity> records = estudiante.getRecords();

        if (records.isEmpty()) {
            return 0.0;
        }

        double sum = records.stream().mapToDouble(RecordEntity::getFinalGrade).sum();
        return sum / records.size();
    }

    

    public Double calculateCourseAverage(String courseCode) {
     if (courseCode == null || courseCode.isEmpty()) {
            throw new IllegalArgumentException("El código del curso no puede ser nulo o vacío.");
        }

        Optional<CourseEntity> cursoOpt = cursoRepository.findByCourseCode(courseCode);
        if (cursoOpt.isEmpty()) {
            throw new IllegalArgumentException("El curso no existe.");
        }

        CourseEntity curso = cursoOpt.get();
        List<RecordEntity> records = inscripcionRepository.findAll();

        List<RecordEntity> courseRecords = records.stream()
                .filter(record -> record.getCourse().getId().equals(curso.getId()))
                .toList();

        if (courseRecords.isEmpty()) {
            return 0.0;
        }

        double sum = courseRecords.stream().mapToDouble(RecordEntity::getFinalGrade).sum();
        return sum / courseRecords.size();
    }

}
