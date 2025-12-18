package com.example.demo.service;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.repository.ServiceCounterRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCounterService {

    private final ServiceCounterRepository serviceCounterRepository;

    public ServiceCounterService(ServiceCounterRepository serviceCounterRepository) {
        this.serviceCounterRepository = serviceCounterRepository;
    }

    public ServiceCounter addCounter(ServiceCounter counter) {
        return serviceCounterRepository.save(counter);
    }

    public List<ServiceCounter> getActiveCounters() {
        return serviceCounterRepository.findByIsActiveTrue();
    }
}