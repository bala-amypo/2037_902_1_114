CONFIG:
JwtTokenProvider:
package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    private final String secretKey;
    private final long expirationMillis;
    private final Key key;
    
    public JwtTokenProvider() {
        this.secretKey = "mySecretKeyForJWTTokenGenerationThatIsLongEnough";
        this.expirationMillis = 86400000; // 24 hours
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    public JwtTokenProvider(String secretKey, long expirationMillis) {
        this.secretKey = secretKey;
        this.expirationMillis = expirationMillis;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

OpenApiConfig:
package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Digital Queue Management System").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

SecurityConfig:
package com.example.demo.config;

import com.example.demo.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    
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
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

CONTROLLER:
AlertController:
package com.example.demo.controller;

import com.example.demo.entity.BreachAlert;
import com.example.demo.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
@Tag(name = "Alerts", description = "Alert management")
public class AlertController {
    
    private final TokenService tokenService;
    
    public AlertController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @PostMapping("/counter/{counterId}")
    @Operation(summary = "Issue new alert")
    public ResponseEntity<BreachAlert> issueAlert(@PathVariable Long counterId) {
        BreachAlert alert = tokenService.issueToken(counterId);
        return ResponseEntity.ok(alert);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update alert status")
    public ResponseEntity<BreachAlert> updateStatus(@PathVariable Long id, @RequestParam String status) {
        BreachAlert alert = tokenService.updateStatus(id, status);
        return ResponseEntity.ok(alert);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get alert details")
    public ResponseEntity<BreachAlert> getAlert(@PathVariable Long id) {
        BreachAlert alert = tokenService.getToken(id);
        return ResponseEntity.ok(alert);
    }
}

AuthController:
package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = new User(request.getName(), request.getEmail(), request.getPassword(), request.getRole());
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.getEmail());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
        
        return ResponseEntity.ok(response);
    }
}

ColdRoomController:
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

QueueContoller:
package com.example.demo.controller;

import com.example.demo.entity.QueuePosition;
import com.example.demo.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@Tag(name = "Queue", description = "Queue management")
public class QueueController {
    
    private final QueueService queueService;
    
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }
    
    @PutMapping("/position/{tokenId}/{newPosition}")
    @Operation(summary = "Update queue position")
    public ResponseEntity<QueuePosition> updatePosition(@PathVariable Long tokenId, @PathVariable Integer newPosition) {
        QueuePosition position = queueService.updateQueuePosition(tokenId, newPosition);
        return ResponseEntity.ok(position);
    }
    
    @GetMapping("/position/{tokenId}")
    @Operation(summary = "Get queue position")
    public ResponseEntity<QueuePosition> getPosition(@PathVariable Long tokenId) {
        QueuePosition position = queueService.getPosition(tokenId);
        return ResponseEntity.ok(position);
    }
}

SensorContoller:
package com.example.demo.controller;

import com.example.demo.entity.SensorDevice;
import com.example.demo.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sensors")
@Tag(name = "Sensors", description = "Sensor management")
public class SensorController {
    
    private final SensorService sensorService;
    
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @PostMapping
    @Operation(summary = "Create sensor")
    public ResponseEntity<SensorDevice> createSensor(@RequestBody SensorDevice sensor) {
        SensorDevice saved = sensorService.createSensor(sensor);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    @Operation(summary = "Get all sensors")
    public ResponseEntity<List<SensorDevice>> getAllSensors() {
        List<SensorDevice> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update sensor status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        // This would need to be implemented in the service
        return ResponseEntity.ok("Status updated");
    }
}

ServiceCounterController:
package com.example.demo.controller;

import com.example.demo.entity.ServiceCounter;
import com.example.demo.service.ServiceCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/counters")
@Tag(name = "Counters", description = "Service counter management")
public class ServiceCounterController {
    
    private final ServiceCounterService serviceCounterService;
    
    public ServiceCounterController(ServiceCounterService serviceCounterService) {
        this.serviceCounterService = serviceCounterService;
    }
    
    @PostMapping
    @Operation(summary = "Add service counter")
    public ResponseEntity<ServiceCounter> addCounter(@RequestBody ServiceCounter counter) {
        ServiceCounter saved = serviceCounterService.addCounter(counter);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active counters")
    public ResponseEntity<List<ServiceCounter>> getActiveCounters() {
        List<ServiceCounter> counters = serviceCounterService.getActiveCounters();
        return ResponseEntity.ok(counters);
    }
}

TemperatureReadingController:
package com.example.demo.controller;

import com.example.demo.dto.TemperatureReadingRequest;
import com.example.demo.entity.TemperatureReading;
import com.example.demo.service.TemperatureReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/readings")
@Tag(name = "Readings", description = "Temperature reading management")
public class TemperatureReadingController {
    
    private final TemperatureReadingService temperatureReadingService;
    
    public TemperatureReadingController(TemperatureReadingService temperatureReadingService) {
        this.temperatureReadingService = temperatureReadingService;
    }
    
    @PostMapping
    @Operation(summary = "Record temperature reading")
    public ResponseEntity<TemperatureReading> recordReading(@RequestBody TemperatureReadingRequest request) {
        TemperatureReading reading = temperatureReadingService.recordReading(
                request.getSensorIdentifier(), request.getReadingValue());
        return ResponseEntity.ok(reading);
    }
}

TokenLogController:
package com.example.demo.controller;

import com.example.demo.entity.TokenLog;
import com.example.demo.service.TokenLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "Log management")
public class TokenLogController {
    
    private final TokenLogService tokenLogService;
    
    public TokenLogController(TokenLogService tokenLogService) {
        this.tokenLogService = tokenLogService;
    }
    
    @PostMapping("/{tokenId}")
    @Operation(summary = "Add log entry")
    public ResponseEntity<TokenLog> addLog(@PathVariable Long tokenId, @RequestParam String message) {
        TokenLog log = tokenLogService.addLog(tokenId, message);
        return ResponseEntity.ok(log);
    }
    
    @GetMapping("/{tokenId}")
    @Operation(summary = "Get logs for token")
    public ResponseEntity<List<TokenLog>> getLogs(@PathVariable Long tokenId) {
        List<TokenLog> logs = tokenLogService.getLogs(tokenId);
        return ResponseEntity.ok(logs);
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

ColdRoomRequest:
package com.example.demo.dto;

public class ColdRoomRequest {
    private String name;
    private String location;
    private Double minAllowed;
    private Double maxAllowed;
    
    public ColdRoomRequest() {}
    
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
    
    public ColdRoomResponse() {}
    
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

TemperatureReadingRequest:
package com.example.demo.dto;

public class TemperatureReadingRequest {
    private String sensorIdentifier;
    private Double readingValue;
    
    public TemperatureReadingRequest() {}
    
    public String getSensorIdentifier() { return sensorIdentifier; }
    public void setSensorIdentifier(String sensorIdentifier) { this.sensorIdentifier = sensorIdentifier; }
    
    public Double getReadingValue() { return readingValue; }
    public void setReadingValue(Double readingValue) { this.readingValue = readingValue; }
}

ENTITY:
BreachAlert:
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "breach_alerts")
public class BreachAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String tokenNumber;
    
    @ManyToOne
    @JoinColumn(name = "cold_room_id")
    private ColdRoom coldRoom;
    
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private SensorDevice sensor;
    
    @ManyToOne
    @JoinColumn(name = "reading_id")
    private TemperatureReading reading;
    
    @ManyToOne
    @JoinColumn(name = "service_counter_id")
    private ServiceCounter serviceCounter;
    
    private String status;
    private String breachType;
    private LocalDateTime issuedAt;
    private LocalDateTime resolvedAt;
    
    public BreachAlert() {}
    
    public BreachAlert(String tokenNumber, ColdRoom coldRoom, SensorDevice sensor, TemperatureReading reading, String status, String breachType, LocalDateTime issuedAt, LocalDateTime resolvedAt) {
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
    
    public ServiceCounter getServiceCounter() { return serviceCounter; }
    public void setServiceCounter(ServiceCounter serviceCounter) { this.serviceCounter = serviceCounter; }
    
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

import jakarta.persistence.*;

@Entity
@Table(name = "cold_rooms")
public class ColdRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private BreachAlert token;
    
    private Integer position;
    private LocalDateTime updatedAt;
    
    public QueuePosition() {}
    
    public QueuePosition(BreachAlert token, Integer position, LocalDateTime updatedAt) {
        this.token = token;
        this.position = position;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BreachAlert getToken() { return token; }
    public void setToken(BreachAlert token) { this.token = token; }
    
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

SensorDevice:
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sensor_devices")
public class SensorDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String identifier;
    
    @ManyToOne
    @JoinColumn(name = "cold_room_id")
    private ColdRoom coldRoom;
    
    private Boolean isActive;
    
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
    
    private String name;
    private Boolean isActive;
    
    public ServiceCounter() {}
    
    public ServiceCounter(String name, Boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

TemperatureReading:
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temperature_readings")
public class TemperatureReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private SensorDevice sensor;
    
    @ManyToOne
    @JoinColumn(name = "cold_room_id")
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
    private BreachAlert token;
    
    private String logMessage;
    private LocalDateTime loggedAt;
    
    public TokenLog() {}
    
    public TokenLog(BreachAlert token, String logMessage, LocalDateTime loggedAt) {
        this.token = token;
        this.logMessage = logMessage;
        this.loggedAt = loggedAt;
    }
    
    @PrePersist
    public void prePersist() {
        if (loggedAt == null) {
            loggedAt = LocalDateTime.now();
        }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BreachAlert getToken() { return token; }
    public void setToken(BreachAlert token) { this.token = token; }
    
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
    
    private String role;
    
    public User() {}
    
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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
GlobalExceptionHandling:
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

ResourceNotFound:
package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

REPOSITORY:
ColdRoomRepository:
cc

SensorDeviceRepository:
package com.example.demo.repository;

import com.example.demo.entity.SensorDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SensorDeviceRepository extends JpaRepository<SensorDevice, Long> {
    Optional<SensorDevice> findByIdentifier(String identifier);
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

TemperatureReadingRepository:
package com.example.demo.repository;

import com.example.demo.entity.TemperatureReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {
}

TokenLogRepository:
package com.example.demo.repository;

import com.example.demo.entity.TokenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TokenLogRepository extends JpaRepository<TokenLog, Long> {
    List<TokenLog> findByTokenIdOrderByLoggedAtAsc(Long tokenId);
}

TokenRepository:
package com.example.demo.repository;

import com.example.demo.entity.BreachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<BreachAlert, Long> {
    Optional<BreachAlert> findByTokenNumber(String tokenNumber);
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
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
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
    }
}

JwtFilter:
package com.example.demo.security;

import com.example.demo.config.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtTokenProvider.validateToken(token)) {
                Claims claims = jwtTokenProvider.getClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

SERVICE:
IMPL:
ColdRoomServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.ColdRoom;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ColdRoomRepository;
import com.example.demo.service.ColdRoomService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColdRoomServiceImpl implements ColdRoomService {
    
    private final ColdRoomRepository coldRoomRepository;
    
    public ColdRoomServiceImpl(ColdRoomRepository coldRoomRepository) {
        this.coldRoomRepository = coldRoomRepository;
    }
    
    @Override
    public ColdRoom createColdRoom(ColdRoom coldRoom) {
        if (coldRoom.getMinAllowed() >= coldRoom.getMaxAllowed()) {
            throw new IllegalArgumentException("Invalid temperature range");
        }
        return coldRoomRepository.save(coldRoom);
    }
    
    @Override
    public List<ColdRoom> getAllColdRooms() {
        return coldRoomRepository.findAll();
    }
    
    @Override
    public ColdRoom findById(Long id) {
        return coldRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ColdRoom not found"));
    }
}

QueueServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.QueuePosition;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.QueueService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class QueueServiceImpl implements QueueService {
    
    private final QueuePositionRepository queuePositionRepository;
    private final TokenRepository tokenRepository;
    
    public QueueServiceImpl(QueuePositionRepository queuePositionRepository, TokenRepository tokenRepository) {
        this.queuePositionRepository = queuePositionRepository;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public QueuePosition updateQueuePosition(Long tokenId, Integer newPosition) {
        if (newPosition < 1) {
            throw new IllegalArgumentException("Position must be at least 1");
        }
        
        QueuePosition queuePosition = queuePositionRepository.findByTokenId(tokenId)
                .orElse(new QueuePosition());
        
        if (queuePosition.getToken() == null) {
            queuePosition.setToken(tokenRepository.findById(tokenId)
                    .orElseThrow(() -> new RuntimeException("Token not found")));
        }
        
        queuePosition.setPosition(newPosition);
        queuePosition.setUpdatedAt(LocalDateTime.now());
        
        return queuePositionRepository.save(queuePosition);
    }
    
    @Override
    public QueuePosition getPosition(Long tokenId) {
        return queuePositionRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Queue position not found"));
    }
}

SensorServiceImpl:

package com.example.demo.service.impl;

import com.example.demo.entity.SensorDevice;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SensorDeviceRepository;
import com.example.demo.service.SensorService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {
    
    private final SensorDeviceRepository sensorDeviceRepository;
    
    public SensorServiceImpl(SensorDeviceRepository sensorDeviceRepository) {
        this.sensorDeviceRepository = sensorDeviceRepository;
    }
    
    @Override
    public SensorDevice createSensor(SensorDevice sensor) {
        if (sensorDeviceRepository.findByIdentifier(sensor.getIdentifier()).isPresent()) {
            throw new IllegalArgumentException("Sensor identifier already exists");
        }
        return sensorDeviceRepository.save(sensor);
    }
    
    @Override
    public SensorDevice findByIdentifier(String identifier) {
        return sensorDeviceRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor not found"));
    }
    
    @Override
    public List<SensorDevice> getAllSensors() {
        return sensorDeviceRepository.findAll();
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
    
    private final ServiceCounterRepository serviceCounterRepository;
    
    public ServiceCounterServiceImpl(ServiceCounterRepository serviceCounterRepository) {
        this.serviceCounterRepository = serviceCounterRepository;
    }
    
    @Override
    public ServiceCounter addCounter(ServiceCounter counter) {
        return serviceCounterRepository.save(counter);
    }
    
    @Override
    public List<ServiceCounter> getActiveCounters() {
        return serviceCounterRepository.findByIsActiveTrue();
    }
}

TemperatureReadingServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.BreachAlert;
import com.example.demo.entity.SensorDevice;
import com.example.demo.entity.TemperatureReading;
import com.example.demo.repository.TemperatureReadingRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.SensorService;
import com.example.demo.service.TemperatureReadingService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TemperatureReadingServiceImpl implements TemperatureReadingService {
    
    private final TemperatureReadingRepository temperatureReadingRepository;
    private final SensorService sensorService;
    private final TokenRepository tokenRepository;
    
    public TemperatureReadingServiceImpl(TemperatureReadingRepository temperatureReadingRepository, 
                                       SensorService sensorService, TokenRepository tokenRepository) {
        this.temperatureReadingRepository = temperatureReadingRepository;
        this.sensorService = sensorService;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public TemperatureReading recordReading(String sensorIdentifier, Double readingValue) {
        SensorDevice sensor = sensorService.findByIdentifier(sensorIdentifier);
        
        if (!sensor.getIsActive()) {
            throw new IllegalArgumentException("Sensor is not active");
        }
        
        TemperatureReading reading = new TemperatureReading(sensor, sensor.getColdRoom(), readingValue, LocalDateTime.now());
        reading = temperatureReadingRepository.save(reading);
        
        // Check for breach
        var coldRoom = sensor.getColdRoom();
        if (readingValue < coldRoom.getMinAllowed() || readingValue > coldRoom.getMaxAllowed()) {
            BreachAlert alert = new BreachAlert();
            alert.setTokenNumber(UUID.randomUUID().toString());
            alert.setColdRoom(coldRoom);
            alert.setSensor(sensor);
            alert.setReading(reading);
            alert.setStatus("OPEN");
            alert.setBreachType(readingValue < coldRoom.getMinAllowed() ? "LOW" : "HIGH");
            alert.setIssuedAt(LocalDateTime.now());
            tokenRepository.save(alert);
        }
        
        return reading;
    }
}

TokenLogServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.TokenLog;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenLogServiceImpl implements TokenLogService {
    
    private final TokenLogRepository tokenLogRepository;
    private final TokenRepository tokenRepository;
    
    public TokenLogServiceImpl(TokenLogRepository tokenLogRepository, TokenRepository tokenRepository) {
        this.tokenLogRepository = tokenLogRepository;
        this.tokenRepository = tokenRepository;
    }
    
    @Override
    public TokenLog addLog(Long tokenId, String message) {
        var token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        TokenLog log = new TokenLog(token, message, LocalDateTime.now());
        return tokenLogRepository.save(log);
    }
    
    @Override
    public List<TokenLog> getLogs(Long tokenId) {
        return tokenLogRepository.findByTokenIdOrderByLoggedAtAsc(tokenId);
    }
}

TokenServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.BreachAlert;
import com.example.demo.entity.QueuePosition;
import com.example.demo.entity.ServiceCounter;
import com.example.demo.entity.TokenLog;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QueuePositionRepository;
import com.example.demo.repository.ServiceCounterRepository;
import com.example.demo.repository.TokenLogRepository;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    
    private final TokenRepository tokenRepository;
    private final ServiceCounterRepository serviceCounterRepository;
    private final TokenLogRepository tokenLogRepository;
    private final QueuePositionRepository queuePositionRepository;
    
    public TokenServiceImpl(TokenRepository tokenRepository, ServiceCounterRepository serviceCounterRepository, 
                           TokenLogRepository tokenLogRepository, QueuePositionRepository queuePositionRepository) {
        this.tokenRepository = tokenRepository;
        this.serviceCounterRepository = serviceCounterRepository;
        this.tokenLogRepository = tokenLogRepository;
        this.queuePositionRepository = queuePositionRepository;
    }
    
    @Override
    public BreachAlert issueToken(Long counterId) {
        ServiceCounter counter = serviceCounterRepository.findById(counterId)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found"));
        
        if (!counter.getIsActive()) {
            throw new IllegalArgumentException("Counter is not active");
        }
        
        BreachAlert token = new BreachAlert();
        token.setTokenNumber(UUID.randomUUID().toString());
        token.setStatus("OPEN");
        token.setIssuedAt(LocalDateTime.now());
        token.setServiceCounter(counter);
        
        token = tokenRepository.save(token);
        
        QueuePosition queuePosition = new QueuePosition(token, 1, LocalDateTime.now());
        queuePositionRepository.save(queuePosition);
        
        TokenLog log = new TokenLog(token, "Token issued", LocalDateTime.now());
        tokenLogRepository.save(log);
        
        return token;
    }
    
    @Override
    public BreachAlert updateStatus(Long tokenId, String status) {
        BreachAlert token = getToken(tokenId);
        
        String currentStatus = token.getStatus();
        if (!isValidTransition(currentStatus, status)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + status);
        }
        
        token.setStatus(status);
        if ("RESOLVED".equals(status) || "CANCELLED".equals(status)) {
            token.setResolvedAt(LocalDateTime.now());
        }
        
        token = tokenRepository.save(token);
        
        TokenLog log = new TokenLog(token, "Status updated to " + status, LocalDateTime.now());
        tokenLogRepository.save(log);
        
        return token;
    }
    
    @Override
    public BreachAlert getToken(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }
    
    private boolean isValidTransition(String from, String to) {
        if ("OPEN".equals(from)) {
            return "ACKNOWLEDGED".equals(to) || "CANCELLED".equals(to);
        }
        if ("ACKNOWLEDGED".equals(from)) {
            return "RESOLVED".equals(to) || "CANCELLED".equals(to);
        }
        return false;
    }
}

UserServiceImpl:
package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (user.getRole() == null) {
            user.setRole("STAFF");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

ColdRoomService:
package com.example.demo.service;

import com.example.demo.entity.ColdRoom;
import java.util.List;

public interface ColdRoomService {
    ColdRoom createColdRoom(ColdRoom coldRoom);
    List<ColdRoom> getAllColdRooms();
    ColdRoom findById(Long id);
}

QueueService:
package com.example.demo.service;

import com.example.demo.entity.QueuePosition;

public interface QueueService {
    QueuePosition updateQueuePosition(Long tokenId, Integer newPosition);
    QueuePosition getPosition(Long tokenId);
}

SensorService:
package com.example.demo.service;

import com.example.demo.entity.SensorDevice;
import java.util.List;

public interface SensorService {
    SensorDevice createSensor(SensorDevice sensor);
    SensorDevice findByIdentifier(String identifier);
    List<SensorDevice> getAllSensors();
}

ServiceCounterService:
package com.example.demo.service;

import com.example.demo.entity.ServiceCounter;
import java.util.List;

public interface ServiceCounterService {
    ServiceCounter addCounter(ServiceCounter counter);
    List<ServiceCounter> getActiveCounters();
}

TemperatureReadingServcie:
package com.example.demo.service;

import com.example.demo.entity.TemperatureReading;

public interface TemperatureReadingService {
    TemperatureReading recordReading(String sensorIdentifier, Double readingValue);
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

import com.example.demo.entity.BreachAlert;

public interface TokenService {
    BreachAlert issueToken(Long counterId);
    BreachAlert updateStatus(Long tokenId, String status);
    BreachAlert getToken(Long tokenId);
}

UserService:
package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
    User registerUser(User user);
    User findByEmail(String email);
    User findById(Long id);
}


