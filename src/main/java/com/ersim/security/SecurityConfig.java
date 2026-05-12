package com.ersim.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration.
 *
 * Role access rules:
 *   NURSE  — admit patients, view queue / rooms / report, change triage level
 *   DOCTOR — discharge patients, view queue / rooms / report
 *   ADMIN  — all of the above
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/", "/index.html").permitAll()
                // Role-gated REST endpoints
                .requestMatchers(HttpMethod.GET,    "/queue", "/rooms", "/report")
                        .hasAnyRole("ADMIN", "NURSE", "DOCTOR")
                .requestMatchers(HttpMethod.POST,   "/patients")
                        .hasAnyRole("ADMIN", "NURSE")
                .requestMatchers(HttpMethod.PUT,    "/patients/*/level")
                        .hasAnyRole("ADMIN", "NURSE")
                .requestMatchers(HttpMethod.DELETE, "/patients/*")
                        .hasAnyRole("ADMIN", "DOCTOR")
                .anyRequest().authenticated()
            )
            // Allow H2 console iframes
            .headers(h -> h.frameOptions(f -> f.disable()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
