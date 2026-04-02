package com.skillsBridge.skillBridge.config;

import com.skillsBridge.skillBridge.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 🔐 Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return builder.build();
    }

    // 🔥 MAIN SECURITY CONFIG
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ENABLE CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF (JWT case)
            .csrf(csrf -> csrf.disable())

            //  No session (stateless JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            //  Exception handling
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((req, res, ex) -> {
                        log.error("Unauthorized: {}", ex.getMessage());
                        res.sendError(401, "Unauthorized");
                    })
                    .accessDeniedHandler((req, res, ex) -> {
                        log.error("Forbidden: {}", ex.getMessage());
                        res.sendError(403, "Forbidden");
                    })
            )

            //  Authorization rules
            .authorizeHttpRequests(auth -> auth

                //  allow preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //  Public endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                //  Everything else secured
                .anyRequest().authenticated()
            )

            //  JWT Filter
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  CORS CONFIG (VERY IMPORTANT)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173")); // frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}