CONFIG:
JwtTokenProvider:
package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtTokenProvider {
    private final String secretKey;
    private final long expirationMillis;
    private final Key key;

    public JwtTokenProvider(String secretKey, long expirationMillis) {
        this.secretKey = secretKey;
        this.expirationMillis = expirationMillis;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}

SecurityConfig:
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}

CONTROLLER:
AlertController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @GetMapping
    public ResponseEntity<String> getAllAlerts() {
        return ResponseEntity.ok("List of alerts");
    }

    @PostMapping
    public ResponseEntity<String> createAlert(@RequestBody Object alertRequest) {
        return ResponseEntity.ok("Alert created successfully");
    }
}

AuthController:
package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(new AuthResponse("dummy-token", 1L, request.getEmail(), "STAFF"));
    }
}

ColdRoomController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cold-rooms")
public class ColdRoomController {

    @GetMapping
    public ResponseEntity<String> getAllColdRooms() {
        return ResponseEntity.ok("List of cold rooms");
    }

    @PostMapping
    public ResponseEntity<String> createColdRoom(@RequestBody Object coldRoomRequest) {
        return ResponseEntity.ok("Cold room created successfully");
    }
}

QueueController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @PutMapping("/position/{tokenId}/{newPosition}")
    public ResponseEntity<String> updateQueuePosition(@PathVariable Long tokenId, @PathVariable Integer newPosition) {
        return ResponseEntity.ok("Queue position updated for token: " + tokenId + " to position: " + newPosition);
    }

    @GetMapping("/position/{tokenId}")
    public ResponseEntity<String> getQueuePosition(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Queue position for token: " + tokenId);
    }
}

SensorController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    @GetMapping
    public ResponseEntity<String> getAllSensors() {
        return ResponseEntity.ok("List of sensors");
    }

    @PostMapping
    public ResponseEntity<String> createSensor(@RequestBody Object sensorRequest) {
        return ResponseEntity.ok("Sensor created successfully");
    }
}

TemperatureReadingController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/readings")
public class TemperatureReadingController {

    @PostMapping
    public ResponseEntity<String> createReading(@RequestBody Object readingRequest) {
        return ResponseEntity.ok("Temperature reading created successfully");
    }

    @GetMapping("/cold-room/{coldRoomId}")
    public ResponseEntity<String> getReadingsByColdRoom(@PathVariable Long coldRoomId) {
        return ResponseEntity.ok("Readings for cold room: " + coldRoomId);
    }
}

TokenController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @PostMapping("/issue/{counterId}")
    public ResponseEntity<String> issueToken(@PathVariable Long counterId) {
        return ResponseEntity.ok("Token issued for counter: " + counterId);
    }

    @PutMapping("/{tokenId}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long tokenId, @RequestParam String status) {
        return ResponseEntity.ok("Token " + tokenId + " status updated to: " + status);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<String> getToken(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Token details for ID: " + tokenId);
    }
}

TokenLogController:
package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class TokenLogController {

    @PostMapping("/{tokenId}")
    public ResponseEntity<String> addLog(@PathVariable Long tokenId, @RequestBody String message) {
        return ResponseEntity.ok("Log added for token: " + tokenId);
    }

    @GetMapping("/{tokenId}")
    public ResponseEntity<String> getLogs(@PathVariable Long tokenId) {
        return ResponseEntity.ok("Logs for token: " + tokenId);
    }
}

DTO:
AuthRequest:
package com.example.demo.dto;

public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

AuthResponse:
package com.example.demo.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

BreachAlertResponse:
package com.example.demo.dto;

import java.time.LocalDateTime;

public class BreachAlertResponse {
    private Long id;
    private String tokenNumber;
    private String coldRoomName;
    private String sensorIdentifier;
    private String breachType;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDateTime resolvedAt;
    private Integer queuePosition;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public String getColdRoomName() { return coldRoomName; }
    public void setColdRoomName(String coldRoomName) { this.coldRoomName = coldRoomName; }
    public String getSensorIdentifier() { return sensorIdentifier; }
    public void setSensorIdentifier(String sensorIdentifier) { this.sensorIdentifier = sensorIdentifier; }
    public String getBreachType() { return breachType; }
    public void setBreachType(String breachType) { this.breachType = breachType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }
}

ColdRoomRequest:
package com.example.demo.dto;

public class ColdRoomRequest {
    private String name;
    private String location;
    private Double minAllowed;
    private Double maxAllowed;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getMinAllowed() { return minAllowed; }
    public void setMinAllowed(Double minAllowed) { this.minAllowed = minAllowed; }
    public Double getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(Double maxAllowed) { this.maxAllowed = maxAllowed; }
}

ColdRoomResponse:
package com.example.demo.dto;

public class ColdRoomResponse {
    private Long id;
    private String name;
    private String location;
    private Double minAllowed;
    private Double maxAllowed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getMinAllowed() { return minAllowed; }
    public void setMinAllowed(Double minAllowed) { this.minAllowed = minAllowed; }
    public Double getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(Double maxAllowed) { this.maxAllowed = maxAllowed; }
}

QueuePositionResponse:
package com.example.demo.dto;

import java.time.LocalDateTime;

public class QueuePositionResponse {
    private String tokenNumber;
    private Integer position;
    private LocalDateTime updatedAt;

    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

RegisterRequest:
package com.example.demo.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;

    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

SensorRequest:
package com.example.demo.dto;

public class SensorRequest {
    private String identifier;
    private Long coldRoomId;
    private Boolean isActive;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public Long getColdRoomId() { return coldRoomId; }
    public void setColdRoomId(Long coldRoomId) { this.coldRoomId = coldRoomId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

SensorResponse:
package com.example.demo.dto;

public class SensorResponse {
    private Long id;
    private String identifier;
    private Long coldRoomId;
    private String coldRoomName;
    private Boolean isActive;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public Long getColdRoomId() { return coldRoomId; }
    public void setColdRoomId(Long coldRoomId) { this.coldRoomId = coldRoomId; }
    public String getColdRoomName() { return coldRoomName; }
    public void setColdRoomName(String coldRoomName) { this.coldRoomName = coldRoomName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

TemperatureReadingRequest:
package com.example.demo.dto;

public class TemperatureReadingRequest {
    private String sensorIdentifier;
    private Long sensorId;
    private Double readingValue;

    public String getSensorIdentifier() { return sensorIdentifier; }
    public void setSensorIdentifier(String sensorIdentifier) { this.sensorIdentifier = sensorIdentifier; }
    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public Double getReadingValue() { return readingValue; }
    public void setReadingValue(Double readingValue) { this.readingValue = readingValue; }
}

TokenLogResponse:
package com.example.demo.dto;

import java.time.LocalDateTime;

public class TokenLogResponse {
    private Long id;
    private String tokenNumber;
    private String logMessage;
    private LocalDateTime loggedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public String getLogMessage() { return logMessage; }
    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}

ENTITY:
BreachAlert:
package com.example.demo.entity;

import java.time.LocalDateTime;

public class BreachAlert {
    private Long id;
    private String tokenNumber;
    private ColdRoom coldRoom;
    private SensorDevice sensor;
    private TemperatureReading reading;
    private String status = "OPEN";
    private String breachType;
    private LocalDateTime issuedAt;
    private LocalDateTime resolvedAt;

    public BreachAlert() {}

    public BreachAlert(String tokenNumber, ColdRoom coldRoom, SensorDevice sensor, 
                      TemperatureReading reading, String status, String breachType, 
                      LocalDateTime issuedAt, LocalDateTime resolvedAt) {
        this.tokenNumber = tokenNumber;
        this.coldRoom = coldRoom;
        this.sensor = sensor;
        this.reading = reading;
        this.status = status;
        this.breachType = breachType;
        this.issuedAt = issuedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public ColdRoom getColdRoom() { return coldRoom; }
    public void setColdRoom(ColdRoom coldRoom) { this.coldRoom = coldRoom; }
    public SensorDevice getSensor() { return sensor; }
    public void setSensor(SensorDevice sensor) { this.sensor = sensor; }
    public TemperatureReading getReading() { return reading; }
    public void setReading(TemperatureReading reading) { this.reading = reading; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBreachType() { return breachType; }
    public void setBreachType(String breachType) { this.breachType = breachType; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}

ColdRoom:
package com.example.demo.entity;

public class ColdRoom {
    private Long id;
    private String name;
    private String location;
    private Double minAllowed;
    private Double maxAllowed;

    public ColdRoom() {}

    public ColdRoom(String name, String location, Double minAllowed, Double maxAllowed) {
        this.name = name;
        this.location = location;
        this.minAllowed = minAllowed;
        this.maxAllowed = maxAllowed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getMinAllowed() { return minAllowed; }
    public void setMinAllowed(Double minAllowed) { this.minAllowed = minAllowed; }
    public Double getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(Double maxAllowed) { this.maxAllowed = maxAllowed; }
}

QueuePosition:
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_positions")
public class QueuePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "token_id")
    private Token token;
    
    private Integer position;
    private LocalDateTime updatedAt;

    public QueuePosition() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

SensorDevice:
package com.example.demo.entity;

public class SensorDevice {
    private Long id;
    private String identifier;
    private ColdRoom coldRoom;
    private Boolean isActive = true;

    public SensorDevice() {}

    public SensorDevice(String identifier, ColdRoom coldRoom, Boolean isActive) {
        this.identifier = identifier;
        this.coldRoom = coldRoom;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public ColdRoom getColdRoom() { return coldRoom; }
    public void setColdRoom(ColdRoom coldRoom) { this.coldRoom = coldRoom; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

ServiceCounter:
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_counters")
public class ServiceCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String counterName;
    private String department;
    private Boolean isActive = true;

    public ServiceCounter() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCounterName() { return counterName; }
    public void setCounterName(String counterName) { this.counterName = counterName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

TemperatureReading:
package com.example.demo.entity;

import java.time.LocalDateTime;

public class TemperatureReading {
    private Long id;
    private SensorDevice sensor;
    private ColdRoom coldRoom;
    private Double readingValue;
    private LocalDateTime recordedAt;

    public TemperatureReading() {}

    public TemperatureReading(SensorDevice sensor, ColdRoom coldRoom, Double readingValue, LocalDateTime recordedAt) {
        this.sensor = sensor;
        this.coldRoom = coldRoom;
        this.readingValue = readingValue;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SensorDevice getSensor() { return sensor; }
    public void setSensor(SensorDevice sensor) { this.sensor = sensor; }
    public ColdRoom getColdRoom() { return coldRoom; }
    public void setColdRoom(ColdRoom coldRoom) { this.coldRoom = coldRoom; }
    public Double getReadingValue() { return readingValue; }
    public void setReadingValue(Double readingValue) { this.readingValue = readingValue; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}

Token:
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String tokenNumber;
    
    @ManyToOne
    @JoinColumn(name = "service_counter_id")
    private ServiceCounter serviceCounter;
    
    private String status = "WAITING";
    private LocalDateTime issuedAt;
    private LocalDateTime completedAt;

    public Token() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(String tokenNumber) { this.tokenNumber = tokenNumber; }
    public ServiceCounter getServiceCounter() { return serviceCounter; }
    public void setServiceCounter(ServiceCounter serviceCounter) { this.serviceCounter = serviceCounter; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

TokenLog:
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_logs")
public class TokenLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "token_id")
    private Token token;
    
    private String logMessage;
    
    @Column(name = "logged_at")
    private LocalDateTime loggedAt = LocalDateTime.now();

    public TokenLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
    public String getLogMessage() { return logMessage; }
    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}

User:
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    
    private String role = "STAFF";

    public User() {}

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "STAFF";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

EXCEPTION:
GlobalExceptionHandler:
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}

ResourceNotFoundException:
package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

REPOSITORY:
QueuePositionRepository:
package com.example.demo.repository;

import com.example.demo.entity.QueuePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QueuePositionRepository extends JpaRepository<QueuePosition, Long> {
    Optional<QueuePosition> findByToken_Id(Long tokenId);
}

ServiceCounterRepository:
package com.example.demo.repository;

import com.example.demo.entity.ServiceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceCounterRepository extends JpaRepository<ServiceCounter, Long> {
    List<ServiceCounter> findByIsActiveTrue();
}

TokenLogRepository:
package com.example.demo.repository;

import com.example.demo.entity.TokenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TokenLogRepository extends JpaRepository<TokenLog, Long> {
    List<TokenLog> findByToken_IdOrderByLoggedAtAsc(Long tokenId);
}

TokenRepository:
package com.example.demo.repository;

import com.example.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenNumber(String tokenNumber);
    List<Token> findByServiceCounter_IdAndStatusOrderByIssuedAtAsc(Long counterId, String status);
}

UserRepository:
package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

SECURITY:
CustomUserDetailsService:
package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
    }
}

JwtFilter:
package com.example.demo.security;

import com.example.demo.config.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getClaims(token).getSubject();
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

JwtUtil:
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secretKey = "ChangeThisSecretKeyReplaceMe1234567890";
    private final long expirationMillis = 3600000;
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}

SERVICE:
QueueService:
package com.example.demo.service;

import com.example.demo.entity.QueuePosition;

public interface QueueService {
    QueuePosition updateQueuePosition(Long tokenId, Integer newPosition);
    QueuePosition getPosition(Long tokenId);
}

ServiceCounterService:
package com.example.demo.service;

import com.example.demo.entity.ServiceCounter;
import java.util.List;

public interface ServiceCounterService {
    ServiceCounter addCounter(ServiceCounter counter);
    List<ServiceCounter> getActiveCounters();
}

TokenLogService:
package com.example.demo.service;

import com.example.demo.entity.TokenLog;
import java.util.List;

public interface TokenLogService {
    TokenLog addLog(Long tokenId, String message);
    List<TokenLog> getLogs(Long tokenId);
}

TokenService:
package com.example.demo.service;

import com.example.demo.entity.Token;

public interface TokenService {
    Token issueToken(Long counterId);
    Token updateStatus(Long tokenId, String status);
    Token getToken(Long tokenId);
}

UserService:
package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
    User register(User user);
    User findByEmail(String email);
    User findById(Long id);
}

IMPL:
QueueServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.QueuePosition;
import com.example.demo.entity.Token;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.QueueService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class QueueServiceImpl implements QueueService {
    private final QueuePositionRepository queueRepository;
    private final TokenRepository tokenRepository;

    public QueueServiceImpl(QueuePositionRepository queueRepository, TokenRepository tokenRepository) {
        this.queueRepository = queueRepository;
        this.tokenRepository = tokenRepository;
    }

    public QueuePosition updateQueuePosition(Long tokenId, Integer newPosition) {
        if (newPosition < 1) {
            throw new IllegalArgumentException("Position must be >= 1");
        }
        
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        QueuePosition queuePosition = queueRepository.findByToken_Id(tokenId)
                .orElse(new QueuePosition());
        
        queuePosition.setToken(token);
        queuePosition.setPosition(newPosition);
        queuePosition.setUpdatedAt(LocalDateTime.now());
        
        QueuePosition saved = queueRepository.save(queuePosition);
        return saved != null ? saved : queuePosition;
    }

    public QueuePosition getPosition(Long tokenId) {
        return queueRepository.findByToken_Id(tokenId)
                .orElseThrow(() -> new RuntimeException("Queue position not found"));
    }
}

ServiceCounterServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.service.ServiceCounterService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceCounterServiceImpl implements ServiceCounterService {
    private final ServiceCounterRepository counterRepository;

    public ServiceCounterServiceImpl(ServiceCounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    public ServiceCounter addCounter(ServiceCounter counter) {
        ServiceCounter saved = counterRepository.save(counter);
        return saved != null ? saved : counter;
    }

    public List<ServiceCounter> getActiveCounters() {
        return counterRepository.findByIsActiveTrue();
    }
}

TokenLogServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.Token;
import com.example.demo.entity.TokenLog;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenLogServiceImpl implements TokenLogService {
    private final TokenLogRepository logRepository;
    private final TokenRepository tokenRepository;

    public TokenLogServiceImpl(TokenLogRepository logRepository, TokenRepository tokenRepository) {
        this.logRepository = logRepository;
        this.tokenRepository = tokenRepository;
    }

    public TokenLog addLog(Long tokenId, String message) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        TokenLog log = new TokenLog();
        log.setToken(token);
        log.setLogMessage(message);
        log.setLoggedAt(LocalDateTime.now());
        
        TokenLog saved = logRepository.save(log);
        return saved != null ? saved : log;
    }

    public List<TokenLog> getLogs(Long tokenId) {
        return logRepository.findByToken_IdOrderByLoggedAtAsc(tokenId);
    }
}

TokenServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.entity.Token;
import com.example.demo.entity.TokenLog;
import com.example.demo.entity.QueuePosition;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.service.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository counterRepository;
    private final TokenLogRepository logRepository;
    private final QueuePositionRepository queueRepository;

    public TokenServiceImpl(TokenRepository tokenRepository, ServiceCounterRepository counterRepository, 
                           TokenLogRepository logRepository, QueuePositionRepository queueRepository) {
        this.tokenRepository = tokenRepository;
        this.counterRepository = counterRepository;
        this.logRepository = logRepository;
        this.queueRepository = queueRepository;
    }

    public Token issueToken(Long counterId) {
        ServiceCounter counter = counterRepository.findById(counterId)
                .orElseThrow(() -> new RuntimeException("Counter not found"));
        
        if (!counter.getIsActive()) {
            throw new IllegalArgumentException("Counter is not active");
        }

        Token token = new Token();
        token.setTokenNumber("T-" + System.currentTimeMillis());
        token.setServiceCounter(counter);
        token.setStatus("WAITING");
        token.setIssuedAt(LocalDateTime.now());
        
        Token saved = tokenRepository.save(token);
        Token finalToken = saved != null ? saved : token;
        
        // Create queue position
        List<Token> waitingTokens = tokenRepository.findByServiceCounter_IdAndStatusOrderByIssuedAtAsc(counterId, "WAITING");
        QueuePosition queuePosition = new QueuePosition();
        queuePosition.setToken(finalToken);
        queuePosition.setPosition(waitingTokens.size());
        queuePosition.setUpdatedAt(LocalDateTime.now());
        queueRepository.save(queuePosition);
        
        // Create log
        TokenLog log = new TokenLog();
        log.setToken(finalToken);
        log.setLogMessage("Token issued");
        logRepository.save(log);
        
        return finalToken;
    }

    public Token updateStatus(Long tokenId, String newStatus) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        String currentStatus = token.getStatus();
        
        // Validate status transitions
        if ("WAITING".equals(currentStatus) && "COMPLETED".equals(newStatus)) {
            throw new IllegalArgumentException("Invalid status transition");
        }
        
        token.setStatus(newStatus);
        
        if ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
            token.setCompletedAt(LocalDateTime.now());
        }
        
        Token saved = tokenRepository.save(token);
        
        // Create log
        TokenLog log = new TokenLog();
        log.setToken(saved != null ? saved : token);
        log.setLogMessage("Status updated to " + newStatus);
        logRepository.save(log);
        
        return saved != null ? saved : token;
    }

    public Token getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
}

UserServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getRole() == null) {
            user.setRole("STAFF");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return saved != null ? saved : user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findById(Long id) {
        throw new RuntimeException("User not found");
    }
}


