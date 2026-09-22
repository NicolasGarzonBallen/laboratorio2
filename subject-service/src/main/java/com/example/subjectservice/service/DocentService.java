package com.example.subjectservice.service;

import com.example.subjectservice.DTO.DocentDTO;
import com.example.subjectservice.DTO.DocentResponseDTO;
import com.example.subjectservice.management.DocentRepository;
import com.example.subjectservice.model.Docent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocentService {
    private final DocentRepository docentRepository;

    public Page<DocentResponseDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(sortDirection.toUpperCase());
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        return docentRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public DocentResponseDTO getById(Long id) {
        Docent docent = docentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + id));
        return toResponseDTO(docent);
    }

    public boolean existsById(Long id) {
        return docentRepository.existsById(id);
    }

    @Transactional
    public DocentResponseDTO create(DocentDTO dto) {
        Docent docent = new Docent();
        docent.setName(dto.getName());
        docent.setCertifications(dto.getCertifications());
        docent.setTypeContract(dto.getTypeContract());
        docent.setSalary(dto.getSalary());
        docent.setAcademy(dto.getAcademy());
        docent.setEmail(dto.getEmail());
        return toResponseDTO(docentRepository.save(docent));
    }

    @Transactional
    public DocentResponseDTO update(Long id, DocentDTO dto) {
        Docent docent = docentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado con id: " + id));
        docent.setName(dto.getName());
        docent.setCertifications(dto.getCertifications());
        docent.setTypeContract(dto.getTypeContract());
        docent.setSalary(dto.getSalary());
        docent.setAcademy(dto.getAcademy());
        docent.setEmail(dto.getEmail());
        return toResponseDTO(docentRepository.save(docent));
    }

    @Transactional
    public void delete(Long id) {
        if (!docentRepository.existsById(id)) {
            throw new RuntimeException("Docente no encontrado con id: " + id);
        }
        docentRepository.deleteById(id);
    }

    private DocentResponseDTO toResponseDTO(Docent docent) {
        return new DocentResponseDTO(docent.getId(), docent.getName(), docent.getCertifications(),
            docent.getTypeContract(), docent.getSalary(), docent.getAcademy(), docent.getEmail());
    }
}
