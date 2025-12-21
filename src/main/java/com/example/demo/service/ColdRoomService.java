package com.example.demo.service;

import com.example.demo.entity.ColdRoom;
import java.util.List;

public interface ColdRoomService {
    ColdRoom createColdRoom(ColdRoom coldRoom);
    List<ColdRoom> getAllColdRooms();
    ColdRoom findById(Long id);
}

