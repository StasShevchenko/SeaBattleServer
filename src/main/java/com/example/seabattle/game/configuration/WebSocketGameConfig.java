package com.example.seabattle.game.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Класс конфигурации веб-сокет соединения с
 * сервером
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketGameConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Метод для добавления конечной точки подключения
     * и допустимых адресов подключающихся с помощью
     * SockJS класса javascript
     * @param registry
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/seabattle")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Метод для регистрации префикса отправляемых на
     * сервер веб-сокет сообщений и регистрации
     * точек рассылки веб-сокет сообщений
     * @param registry
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/private");
    }

}


