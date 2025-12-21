package com.example.demo.service;

import com.example.demo.entity.ServiceCounter;
import java.util.List;

public interface ServiceCounterService {
    ServiceCounter addCounter(ServiceCounter counter);
    List<ServiceCounter> getActiveCounters();
}