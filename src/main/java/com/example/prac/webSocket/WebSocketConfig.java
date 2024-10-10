package com.example.prac.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MusicWebSocketHandler musicWebSocketHandler;
    private final AdminWebSocketHandler adminWebSocketHandler;
    public WebSocketConfig(MusicWebSocketHandler musicWebSocketHandler, AdminWebSocketHandler adminWebSocketHandler) {
        this.musicWebSocketHandler = musicWebSocketHandler;
        this.adminWebSocketHandler = adminWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(musicWebSocketHandler, "/ws/music").setAllowedOrigins("*");
        registry.addHandler(adminWebSocketHandler, "/ws/admin").setAllowedOrigins("*");    }
}
