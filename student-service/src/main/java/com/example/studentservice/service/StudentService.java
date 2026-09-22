package com.example.studentservice.service;

import com.example.studentservice.DTO.*;
import com.example.studentservice.model.Programa;
import com.example.studentservice.model.Student;
import com.example.studentservice.management.ProgramaRepository;
import com.example.studentservice.management.StudentRepository;
import com.example.studentservice.management.StudentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final ProgramaRepository programaRepository;

    public Page<StudentResponseDTO> getAllStudents(PaginationDTO pagination, FilterDTO filter) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(pagination.getSortDirection().toUpperCase());
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }

        String sortField = pagination.getSortBy();
        if ("code".equalsIgnoreCase(sortField)) sortField = "codeNumber";

        Pageable pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), Sort.by(direction, sortField));

        if (filter != null && hasFilter(filter)) {
            return studentRepository.findAll(StudentSpecification.filterStudents(filter), pageable)
                .map(this::toResponseDTO);
        }
        return studentRepository.findAll(pageable).map(this::toResponseDTO);
    }

    private boolean hasFilter(FilterDTO f) {
        return f.getName() != null || f.getLastName() != null || f.getCode() != null ||
               f.getBirthYear() != null || f.getBirthYearFrom() != null || f.getBirthYearTo() != null ||
               f.getProgramId() != null;
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
        return toResponseDTO(student);
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }

    @Transactional
    public StudentResponseDTO createStudent(StudentDTO dto) {
        if (studentRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe un estudiante con codigo " + dto.getCode());
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un estudiante con email " + dto.getEmail());
        }
        Programa programa = programaRepository.findById(dto.getProgramId())
            .orElseThrow(() -> new RuntimeException("Programa no encontrado con id: " + dto.getProgramId()));

        Student student = new Student();
        student.setName(dto.getName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setCode(dto.getCode());
        student.setBirthYear(dto.getBirthYear());
        student.setEntryYear(dto.getEntryYear());
        student.setPrograma(programa);

        return toResponseDTO(studentRepository.save(student));
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        if (!student.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un estudiante con email " + dto.getEmail());
        }

        Programa programa = programaRepository.findById(dto.getProgramId())
            .orElseThrow(() -> new RuntimeException("Programa no encontrado con id: " + dto.getProgramId()));

        student.setName(dto.getName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setBirthYear(dto.getBirthYear());
        student.setEntryYear(dto.getEntryYear());
        student.setPrograma(programa);

        return toResponseDTO(studentRepository.save(student));
    }

    @Transactional
    public StudentResponseDTO patchStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        if (dto.getName() != null) student.setName(dto.getName());
        if (dto.getLastName() != null) student.setLastName(dto.getLastName());
        if (dto.getEmail() != null) student.setEmail(dto.getEmail());
        if (dto.getBirthYear() != 0) student.setBirthYear(dto.getBirthYear());
        if (dto.getProgramId() != null) {
            Programa programa = programaRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con id: " + dto.getProgramId()));
            student.setPrograma(programa);
        }

        return toResponseDTO(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Estudiante no encontrado con id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional
    public void initializeStudents() {
        if (studentRepository.count() == 0) {
            if (programaRepository.count() == 0) {
                throw new RuntimeException("Primero inicializa los programas: POST /api/initialize/programs");
            }
            String[] names = {"Juan", "Maria", "Carlos", "Ana", "Luis", "Laura", "Pedro", "Sofia",
                             "Miguel", "Elena", "Pablo", "Carmen", "Jorge", "Isabel", "David"};
            String[] lastNames = {"Garcia", "Martinez", "Lopez", "Gonzalez", "Rodriguez", "Perez",
                                 "Sanchez", "Ramirez", "Torres", "Flores", "Rivera", "Morales"};
            var programas = programaRepository.findAll();
            Random random = new Random();

            for (int i = 1; i <= 1000; i++) {
                String name = names[random.nextInt(names.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                int entryYear = random.nextInt(10) + 2016;
                int birthYear = entryYear - (16 + random.nextInt(5));
                String code = String.format("%04d", entryYear) + String.format("%04d", i);

                Student student = new Student();
                student.setName(name);
                student.setLastName(lastName);
                student.setEmail(name.toLowerCase() + "." + lastName.toLowerCase() + i + "@uptc.edu.co");
                student.setCode(code);
                student.setBirthYear(birthYear);
                student.setEntryYear(entryYear);
                student.setPrograma(programas.get(random.nextInt(programas.size())));

                studentRepository.save(student);
            }
        }
    }

    private StudentResponseDTO toResponseDTO(Student student) {
        StudentResponseDTO response = new StudentResponseDTO();
        response.setId(student.getId());
        response.setCode(student.getCode());
        response.setName(student.getName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setBirthYear(student.getBirthYear());
        response.setEntryYear(student.getEntryYear());
        if (student.getPrograma() != null) {
            response.setProgramId(student.getPrograma().getIdProgram());
            response.setProgramName(student.getPrograma().getName());
        }
        return response;
    }
}
