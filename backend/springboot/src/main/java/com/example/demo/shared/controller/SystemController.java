package com.example.demo.shared.controller;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

  @GetMapping("/ping")
  public ResponseEntity<Map<String, Object>> ping() {
    return ResponseEntity.ok(Map.of(
        "status", "ok",
        "message", "API running",
        "timestamp", OffsetDateTime.now().toString()));
  }

  @GetMapping("/info")
  public ResponseEntity<Map<String, Object>> info() {
    return ResponseEntity.ok(Map.of(
        "name", "template-api",
        "type", "spring-boot-template"));
  }
}
