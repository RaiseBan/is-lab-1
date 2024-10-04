package com.example.prac.webSocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MusicWebSocketHandler musicWebSocketHandler;

    public WebSocketConfig(MusicWebSocketHandler musicWebSocketHandler) {
        this.musicWebSocketHandler = musicWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(musicWebSocketHandler, "/ws/music").setAllowedOrigins("*");
    }
}
