package com.example.demo.entity;

import jakarta.persistence.*;
public class User{
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;

    public User(){

    }
    public User(Sting name,String email,String password,String role){
        this.name=name;
        this.email=email;
        this.password=password;
        this.role=role;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String get
}