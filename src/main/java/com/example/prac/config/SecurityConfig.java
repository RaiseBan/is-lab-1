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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Отключаем CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()  // Публичные страницы
                        .requestMatchers("/ws/music/**").permitAll()

                        // Разрешаем доступ к /api/music/ (GET) всем пользователям
                        .requestMatchers(HttpMethod.GET, "/api/music").permitAll()

                        // Закрываем доступ ко всем остальным путям под /api/music/** для неаутентифицированных
                        .requestMatchers("/api/music/**").authenticated()

                        .requestMatchers("/api/v1/auth/verify-token").authenticated()
                        // Настраиваем доступ к другим эндпоинтам
                        .requestMatchers("/api/admin-requests/request").hasRole("USER")  // Только для аутентифицированных с ролью USER
                        .requestMatchers("/api/admin-requests/**").hasRole("ADMIN")  // Остальные запросы только для ADMIN

                        .anyRequest().authenticated()  // Все остальные запросы требуют аутентификации
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Указываем, что приложение stateless (нет сессий)
                )
                .authenticationProvider(authenticationProvider)  // Устанавливаем кастомный провайдер для аутентификации
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Добавляем JWT фильтр перед стандартным фильтром логина

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
