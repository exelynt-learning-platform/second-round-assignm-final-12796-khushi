package com.example.demo.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // ✅ PUBLIC
                .requestMatchers("/auth/**").permitAll()

                // ✅ PRODUCTS (VIEW ONLY PUBLIC)
                .requestMatchers("/products").permitAll()
                .requestMatchers("/products/{id}").permitAll()

                // ✅ ADMIN ONLY (CRITICAL FIX)
                .requestMatchers("/products/add").hasRole("ADMIN")
                .requestMatchers("/products/update/**").hasRole("ADMIN")
                .requestMatchers("/products/delete/**").hasRole("ADMIN")

                // ✅ USER APIs
                .requestMatchers("/cart/**").authenticated()
                .requestMatchers("/orders/**").authenticated()
                .requestMatchers("/payments/**").authenticated()

                // ✅ DEFAULT
                .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, excep) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("Unauthorized");
                })
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}