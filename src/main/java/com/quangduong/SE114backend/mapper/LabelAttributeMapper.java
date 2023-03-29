package com.quangduong.SE114backend.mapper;

import com.quangduong.SE114backend.dto.attribute.LabelAttributeDTO;
import com.quangduong.SE114backend.entity.LabelAttributeEntity;
import com.quangduong.SE114backend.exception.ResourceNotFoundException;
import com.quangduong.SE114backend.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LabelAttributeMapper {

    @Autowired
    private LabelRepository labelRepository;

    public LabelAttributeEntity toEntity(LabelAttributeDTO dto) {
        LabelAttributeEntity entity = new LabelAttributeEntity();
        entity.setName(dto.getName());
        entity.setValue(labelRepository.findById(dto.getLabelId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found label with id: " + dto.getLabelId())));
        return entity;
    }

    public LabelAttributeDTO toDTO(LabelAttributeEntity entity) {
        LabelAttributeDTO dto = new LabelAttributeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLabelId(entity.getValue().getId());
        return dto;
    }

}
