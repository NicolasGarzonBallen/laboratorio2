package com.example.studentservice.service;

import com.example.studentservice.DTO.ContactDTO;
import com.example.studentservice.DTO.ContactResponseDTO;
import com.example.studentservice.management.ContactRepository;
import com.example.studentservice.management.StudentRepository;
import com.example.studentservice.model.Contact;
import com.example.studentservice.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final StudentRepository studentRepository;

    public ContactResponseDTO getContactByStudentId(Long studentId) {
        Contact contact = contactRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Este estudiante no tiene contacto registrado"));
        return toResponseDTO(contact);
    }

    @Transactional
    public ContactResponseDTO createContact(Long studentId, ContactDTO dto) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + studentId));
        if (contactRepository.findByStudentId(studentId).isPresent()) {
            throw new RuntimeException("El estudiante ya tiene un contacto registrado (relacion 1:1)");
        }
        Contact contact = new Contact();
        contact.setAddress(dto.getAddress());
        contact.setPhoneOrCell(dto.getPhoneOrCell());
        contact.setEmergencyContact(dto.getEmergencyContact());
        contact.setStudent(student);
        return toResponseDTO(contactRepository.save(contact));
    }

    @Transactional
    public ContactResponseDTO updateContact(Long studentId, ContactDTO dto) {
        Contact contact = contactRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Este estudiante no tiene contacto registrado"));
        contact.setAddress(dto.getAddress());
        contact.setPhoneOrCell(dto.getPhoneOrCell());
        contact.setEmergencyContact(dto.getEmergencyContact());
        return toResponseDTO(contactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(Long studentId) {
        Contact contact = contactRepository.findByStudentId(studentId)
            .orElseThrow(() -> new RuntimeException("Este estudiante no tiene contacto registrado"));
        contactRepository.delete(contact);
    }

    private ContactResponseDTO toResponseDTO(Contact contact) {
        return new ContactResponseDTO(
            contact.getId(),
            contact.getStudent().getId(),
            contact.getAddress(),
            contact.getPhoneOrCell(),
            contact.getEmergencyContact()
        );
    }
}
