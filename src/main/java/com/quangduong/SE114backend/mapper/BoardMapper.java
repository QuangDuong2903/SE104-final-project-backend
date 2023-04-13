package com.quangduong.SE114backend.mapper;

import com.quangduong.SE114backend.dto.board.BoardDTO;
import com.quangduong.SE114backend.dto.board.BoardDetailsDTO;
import com.quangduong.SE114backend.dto.board.BoardUpdateDTO;
import com.quangduong.SE114backend.dto.user.UserBoardDTO;
import com.quangduong.SE114backend.entity.BoardEntity;
import com.quangduong.SE114backend.exception.ResourceNotFoundException;
import com.quangduong.SE114backend.repository.sql.UserRepository;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TableMapper tableMapper;

    public BoardDTO toDTO(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAdminId(entity.getAdmin().getId());
        if(entity.getMembers().size() > 0)
            dto.setMembersIds(entity.getMembers().stream().map(m -> m.getId()).collect(Collectors.toList()));
        return dto;
    }

    public BoardDetailsDTO toDetailsDTO(BoardEntity entity) {
        BoardDetailsDTO dto = new BoardDetailsDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAdmin(userMapper.userInfoDTO(entity.getAdmin()));
        dto.setMembers(entity.getMembers().stream().map(m -> userMapper.userInfoDTO(m)).collect(Collectors.toList()));
        dto.setTables(entity.getTables().stream()
                        .filter(t -> securityUtils.getCurrentUserId() == entity.getAdmin().getId()
                                || t.getCreatedBy() == securityUtils.getCurrentUser().getEmail()
                                || t.getMembers().stream().anyMatch(m -> m.getId() == securityUtils.getCurrentUserId())
                        )
                        .map(t -> tableMapper.taskDetailsDTO(t))
                        .collect(Collectors.toList())
        );
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
