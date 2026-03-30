package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ REGISTER USER
    public String register(User user) {

        // ✅ VALIDATE USERNAME
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Username is required"
            );
        }

        // ✅ CHECK DUPLICATE USER
        if (repo.findByUsername(user.getUsername()) != null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Username already exists"
            );
        }

        // ✅ VALIDATE PASSWORD
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Password must be at least 6 characters"
            );
        }

        // ✅ HASH PASSWORD
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ SET DEFAULT ROLE
        user.setRole("ROLE_USER");

        repo.save(user);

        return "User Registered Successfully";
    }

    // ✅ LOGIN USER
    public String login(String username, String password) {

        User user = repo.findByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid password"
            );
        }

        // ✅ FIX HERE
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}