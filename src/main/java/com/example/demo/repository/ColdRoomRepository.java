package com.example.demo.repository;

import com.example.demo.entity.ColdRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColdRoomRepository extends JpaRepository<ColdRoom, Long> {
}