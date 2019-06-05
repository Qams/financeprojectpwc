package pl.edu.agh.financeservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
@Component
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/hello").setAllowedOrigins("*");
        registry.addEndpoint("/hello").setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/finance").setAllowedOrigins("*").withSockJS();
//        registry.addEndpoint("/active").setAllowedOrigins("*");
        registry.addEndpoint("/active").setAllowedOrigins("*").withSockJS();
    }
}
