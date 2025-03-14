package com.aplicaciongimnasio.PuraEsencia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Se configura el prefijo para los destinos de mensajes
        config.enableSimpleBroker("/topic");  // Destinos del servidor
        config.setApplicationDestinationPrefixes("/app");  // Destinos del cliente
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registro del punto de conexión WebSocket con CORS habilitado para el frontend
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")  // Origen permitido (ajusta si es necesario)
                .withSockJS();  // Con SockJS por compatibilidad
    }
}
