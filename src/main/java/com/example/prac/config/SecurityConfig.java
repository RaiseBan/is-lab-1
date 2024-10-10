package com.example.prac.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Доступ к статическим ресурсам
                        .requestMatchers("/", "/static/**", "/index.html").permitAll()
                        // Доступ к API
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/ws/music/**").permitAll()
                        .requestMatchers("/ws/admin/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/music").permitAll()
                        .requestMatchers("/api/music/**").authenticated()

                        .requestMatchers("/api/v1/special/**").permitAll()
                        .requestMatchers("/api/v1/special/add-single").authenticated()
                        .requestMatchers("/api/v1/special/remove-participant").authenticated()

                        .requestMatchers("/api/v1/auth/verify-token").authenticated()
                        .requestMatchers("/api/admin-requests/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin-requests/request").hasAuthority("ROLE_USER")
                        // Разрешаем все остальные запросы, если они не аутентифицированы
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");  // Разрешаем запросы с фронтенда
        configuration.addAllowedMethod("*");  // Разрешаем любые HTTP методы
        configuration.addAllowedHeader("*");  // Разрешаем любые заголовки
        configuration.setAllowCredentials(true);  // Разрешаем отправку cookies/credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
