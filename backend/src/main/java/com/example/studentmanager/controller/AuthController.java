package com.example.studentmanager.controller;

import com.example.studentmanager.dto.ApiResponse;
import com.example.studentmanager.dto.LoginRequest;
import com.example.studentmanager.dto.LoginResponse;
import com.example.studentmanager.dto.RegisterRequest;
import com.example.studentmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @PostMapping("/register-student")
    public ApiResponse<String> registerStudent(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registerStudent(request);
            return ApiResponse.success("注册成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/register-teacher")
    public ApiResponse<String> registerTeacher(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registerTeacher(request);
            return ApiResponse.success("教师注册成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<Object> getCurrentUser(Authentication authentication) {
        try {
            Object user = authService.getCurrentUser(authentication.getName());
            return ApiResponse.success(user);
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
}
