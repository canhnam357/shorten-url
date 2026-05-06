package canhnam357.shortenurlproject.service.implementation;

import canhnam357.shortenurlproject.dto.auth.LoginRequest;
import canhnam357.shortenurlproject.dto.auth.SignupRequest;
import canhnam357.shortenurlproject.entity.User;
import canhnam357.shortenurlproject.repository.UserRepository;
import canhnam357.shortenurlproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;


    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> signup(SignupRequest signupRequest) {
        String email = signupRequest.email();
        String password = signupRequest.password();
        String confirmPassword = signupRequest.confirmPassword();
        if (!password.equals(confirmPassword)) return ResponseEntity.badRequest().build();

        User user = User.builder().email(email).password(passwordEncoder.encode(password)).build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
