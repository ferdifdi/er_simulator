package com.ersim.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP-over-WebSocket configuration. Clients subscribe to /topic/*
 * to receive live updates of queue and room state.
 *
 * TODO #Sruthi: configure broker prefixes, application destination
 *               prefix, and the STOMP endpoint with SockJS fallback.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // TODO #Sruthi: enableSimpleBroker("/topic"), setApplicationDestinationPrefixes("/app")
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // TODO #Sruthi: registry.addEndpoint("/ws").withSockJS()
    }
}
