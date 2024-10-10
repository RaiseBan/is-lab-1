package com.example.prac.webSocket;

import com.example.prac.service.auth.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class AdminWebSocketHandler extends TextWebSocketHandler {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public AdminWebSocketHandler(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {        Map<String, String> parameters = getQueryParams(Objects.requireNonNull(session.getUri()).getQuery());
        String token = parameters.get("token");

        if (token != null) {
                        try {
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {                    activeSessions.put(username, session);
                } else {
                                        session.close(CloseStatus.POLICY_VIOLATION);
                }
            } catch (Exception e) {
                                session.close(CloseStatus.POLICY_VIOLATION);
            }
        } else {
                        session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);        activeSessions.values().remove(session);
    }

    private Map<String, String> getQueryParams(String query) {        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }    public void sendNotificationToUser(String username, String message) {
        WebSocketSession session = activeSessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                            }
        } else {
                    }
    }
}
