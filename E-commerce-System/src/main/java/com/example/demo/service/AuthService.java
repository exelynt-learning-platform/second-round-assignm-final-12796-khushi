package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // ✅ ADD

    // ✅ REGISTER
    public String register(User user) {

        // 🔥 HASH PASSWORD BEFORE SAVING
    	user.setPassword(passwordEncoder.encode(user.getPassword()));

    	// 🔥 ADD THIS LINE (VERY IMPORTANT)
    	user.setRole("ROLE_USER");

    	repo.save(user);
        return "User Registered Successfully";
    }

    // ✅ LOGIN
    public String login(String username, String password) {

        User user = repo.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 🔥 COMPARE HASHED PASSWORD
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(username);
    }
}