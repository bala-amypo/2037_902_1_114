package com.example.demo.service.impl;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.service.ColdRoomService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColdRoomServiceImpl implements ColdRoomService {
    
    private final ServiceCounterRepository repository;

    public ColdRoomServiceImpl(ServiceCounterRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceCounter addColdRoom(ServiceCounter coldRoom) {
        return repository.save(coldRoom);
    }

    @Override
    public List<ServiceCounter> getAllColdRooms() {
        return repository.findAll();
    }

    @Override
    public ServiceCounter getColdRoomById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cold room not found"));
    }
}
