package com.quangduong.SE114backend.mapper;

import com.quangduong.SE114backend.dto.board.BoardDTO;
import com.quangduong.SE114backend.dto.board.BoardUpdateDTO;
import com.quangduong.SE114backend.entity.BoardEntity;
import com.quangduong.SE114backend.exception.ResourceNotFoundException;
import com.quangduong.SE114backend.repository.UserRepository;
import com.quangduong.SE114backend.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BoardMapper {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UserRepository userRepository;

    public BoardDTO toDTO(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAdminId(entity.getAdmin().getId());
        if(entity.getMembers().size() > 0)
            dto.setMembersIds(entity.getMembers().stream().map(m -> m.getId()).collect(Collectors.toList()));
        return dto;
    }

    public BoardEntity toEntity(BoardDTO dto) {
        BoardEntity entity = new BoardEntity();
        entity.setName(dto.getName());
        entity.setAdmin(securityUtils.getCurrentUser());
        if(dto.getMembersIds() != null)
            entity.setMembers(dto.getMembersIds().stream()
                    .map(i -> userRepository.findById(i).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + i)))
                    .collect(Collectors.toList())
            );
        return entity;
    }

    public BoardEntity toEntity(BoardUpdateDTO dto, BoardEntity entity) {
        if (dto.getName() != null)
            entity.setName(dto.getName());
        if (dto.getMembersIds() != null)
            entity.setMembers(dto.getMembersIds().stream()
                    .map(i -> userRepository.findById(i).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + i)))
                    .collect(Collectors.toList())
            );
        return entity;
    }
}
