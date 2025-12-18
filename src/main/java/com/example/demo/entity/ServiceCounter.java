package com.example.demo.entity;

import jakarta.persistence.*;
@Entity
public class ServiceCounter{
    private Long id;
    private String counterName;
    private String department;
    private Boolean isActive;

    public ServiceCounter(){

    }

    public ServiceCounter(Long id,String counterName,String department,Boolean isActive){
        this.counterName=counterName;
        this.department=department;
        this.isActive=isActive;
    }
}