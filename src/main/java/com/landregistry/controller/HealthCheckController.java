package com.landregistry.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Land Registry API is online");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "Land Registry Blockchain");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> healthDetailed() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("uptime", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        systemInfo.put("totalMemory", Runtime.getRuntime().totalMemory());
        systemInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
        systemInfo.put("maxMemory", Runtime.getRuntime().maxMemory());
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        response.put("status", "UP");
        response.put("message", "Land Registry API is running");
        response.put("timestamp", System.currentTimeMillis());
        response.put("systemInfo", systemInfo);
        response.put("service", "Land Registry Blockchain");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}