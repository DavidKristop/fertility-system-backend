package com.group3.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.group3.backend.filter.JwtAuthFilter;
import com.group3.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)  
public class SecurityConfig {

    @Lazy
    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/auth/welcome", "/api/auth/signup", "/api/auth/signin",
                            "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                            "/webjars/**", "/swagger-ui.html","/api/treatments","/api/schedules/**",
                            "/api/protocols/**","/api/auth/forgot-password", "/api/auth/reset-password",
                            "/api/payments/patient/process/vnpay/return",
                            "/api/verify-email/**", "/api/auth/refresh"
                    ).permitAll()
                    .requestMatchers("/blogs").hasAuthority("ROLE_PATIENT")
                    .requestMatchers("/api/auth/me").authenticated()
                    .requestMatchers("/api/auth/patient/**").hasAuthority("ROLE_PATIENT")
                    .requestMatchers("/api/auth/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/drugs").hasAnyAuthority("ROLE_DOCTOR", "ROLE_MANAGER")
                    .requestMatchers("/api/drugs/**").hasAnyAuthority("ROLE_DOCTOR", "ROLE_MANAGER")
                    .requestMatchers("/api/services").hasAnyAuthority("ROLE_DOCTOR", "ROLE_MANAGER")
                    .requestMatchers("/api/services/**").hasAnyAuthority("ROLE_DOCTOR", "ROLE_MANAGER")
                    .anyRequest().authenticated()
                )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService); // sử dụng UserService để xác thực
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}