package com.example.inventoryexample.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message", ex.getMessage());
        
        // Jika pesan mengandung "tidak ditemukan", kembalikan 404
        if (ex.getMessage() != null && ex.getMessage().contains("tidak ditemukan")) {
            error.put("status", 404);
            error.put("error", "Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        // Jika pesan mengandung validasi error, kembalikan 400
        if (ex.getMessage() != null && (
            ex.getMessage().contains("sudah digunakan") || 
            ex.getMessage().contains("sudah ada") ||
            ex.getMessage().contains("tidak valid") ||
            ex.getMessage().contains("Tidak dapat"))) {
            error.put("status", 400);
            error.put("error", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
