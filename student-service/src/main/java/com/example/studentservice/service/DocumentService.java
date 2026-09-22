package com.example.studentservice.service;

import com.example.studentservice.DTO.DocumentDTO;
import com.example.studentservice.DTO.DocumentResponseDTO;
import com.example.studentservice.management.DocumentRepository;
import com.example.studentservice.management.StudentRepository;
import com.example.studentservice.model.Document;
import com.example.studentservice.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final StudentRepository studentRepository;

    public List<DocumentResponseDTO> getDocumentsByStudentId(Long studentId) {
        return documentRepository.findByStudentId(studentId).stream().map(this::toResponseDTO).toList();
    }

    @Transactional
    public DocumentResponseDTO addDocument(Long studentId, DocumentDTO dto) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + studentId));
        Document document = new Document();
        document.setIdCard(dto.getIdCard());
        document.setEps(dto.getEps());
        document.setCertificates(dto.getCertificates());
        document.setStudent(student);
        return toResponseDTO(documentRepository.save(document));
    }

    @Transactional
    public DocumentResponseDTO updateDocument(Long documentId, DocumentDTO dto) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado con id: " + documentId));
        document.setIdCard(dto.getIdCard());
        document.setEps(dto.getEps());
        document.setCertificates(dto.getCertificates());
        return toResponseDTO(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new RuntimeException("Documento no encontrado con id: " + documentId);
        }
        documentRepository.deleteById(documentId);
    }

    private DocumentResponseDTO toResponseDTO(Document document) {
        return new DocumentResponseDTO(
            document.getId(),
            document.getStudent().getId(),
            document.getIdCard(),
            document.getEps(),
            document.getCertificates()
        );
    }
}
