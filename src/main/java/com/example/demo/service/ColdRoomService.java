package com.example.demo.service;

import com.example.demo.entity.ServiceCounter;
import java.util.List;

public interface ColdRoomService {
    ServiceCounter addColdRoom(ServiceCounter coldRoom);
    List<ServiceCounter> getAllColdRooms();
    ServiceCounter getColdRoomById(Long id);
}