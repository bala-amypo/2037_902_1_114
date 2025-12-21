package com.example.demo.service.impl;

import com.example.demo.entity.ColdRoom;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ColdRoomRepository;
import com.example.demo.service.ColdRoomService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColdRoomServiceImpl implements ColdRoomService {
    
    private final ColdRoomRepository coldRoomRepository;
    
    public ColdRoomServiceImpl(ColdRoomRepository coldRoomRepository) {
        this.coldRoomRepository = coldRoomRepository;
    }
    
    @Override
    public ColdRoom createColdRoom(ColdRoom coldRoom) {
        if (coldRoom.getMinAllowed() >= coldRoom.getMaxAllowed()) {
            throw new IllegalArgumentException("Invalid temperature range");
        }
        return coldRoomRepository.save(coldRoom);
    }
    
    @Override
    public List<ColdRoom> getAllColdRooms() {
        return coldRoomRepository.findAll();
    }
    
    @Override
    public ColdRoom findById(Long id) {
        return coldRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ColdRoom not found"));
    }
}
