package com.example.demo.controller;

import com.example.demo.dto.ColdRoomRequest;
import com.example.demo.dto.ColdRoomResponse;
import com.example.demo.entity.ColdRoom;
import com.example.demo.service.ColdRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cold-rooms")
@Tag(name = "Cold Rooms", description = "Cold room management")
public class ColdRoomController {
    
    private final ColdRoomService coldRoomService;
    
    public ColdRoomController(ColdRoomService coldRoomService) {
        this.coldRoomService = coldRoomService;
    }
    
    @PostMapping
    @Operation(summary = "Create cold room")
    public ResponseEntity<ColdRoomResponse> createColdRoom(@RequestBody ColdRoomRequest request) {
        ColdRoom coldRoom = new ColdRoom(request.getName(), request.getLocation(), 
                                       request.getMinAllowed(), request.getMaxAllowed());
        ColdRoom saved = coldRoomService.createColdRoom(coldRoom);
        
        ColdRoomResponse response = new ColdRoomResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setLocation(saved.getLocation());
        response.setMinAllowed(saved.getMinAllowed());
        response.setMaxAllowed(saved.getMaxAllowed());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all cold rooms")
    public ResponseEntity<List<ColdRoomResponse>> getAllColdRooms() {
        List<ColdRoom> coldRooms = coldRoomService.getAllColdRooms();
        List<ColdRoomResponse> responses = coldRooms.stream()
                .map(cr -> {
                    ColdRoomResponse response = new ColdRoomResponse();
                    response.setId(cr.getId());
                    response.setName(cr.getName());
                    response.setLocation(cr.getLocation());
                    response.setMinAllowed(cr.getMinAllowed());
                    response.setMaxAllowed(cr.getMaxAllowed());
                    return response;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}