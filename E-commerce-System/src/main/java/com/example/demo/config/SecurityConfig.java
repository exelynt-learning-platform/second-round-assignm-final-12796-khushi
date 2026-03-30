package com.example.demo.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Security Configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ Disable CSRF (for REST APIs)
            .csrf(csrf -> csrf.disable())

            // ✅ Stateless session (IMPORTANT for JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Authorization rules
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()   // login/register
                    .requestMatchers("/products/**").permitAll() // allow product view
                    .anyRequest().authenticated()              // secure others
            )

            // ✅ Handle unauthorized access (IMPORTANT)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, excep) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("Unauthorized");
                })
            )

            // ✅ Add JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}