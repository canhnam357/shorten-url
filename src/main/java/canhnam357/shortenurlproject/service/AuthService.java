package canhnam357.shortenurlproject.service;

import canhnam357.shortenurlproject.dto.auth.LoginRequest;
import canhnam357.shortenurlproject.dto.auth.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> signup(SignupRequest signupRequest);
}
