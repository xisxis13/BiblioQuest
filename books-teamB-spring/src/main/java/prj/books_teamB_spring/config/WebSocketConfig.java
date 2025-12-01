// package prj.books_teamB_spring.config;

// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


// @Configuration
// @EnableWebSocketMessageBroker
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
//     // 1. Déclare le ou les endpoints WebSocket
//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         registry.addEndpoint("/ws")                    // URL de connexion WebSocket
//                 .setAllowedOriginPatterns("*")        // à restreindre en prod (ex: "http://localhost:5173")
//                 .withSockJS();                        // fallback SockJS (optionnel, mais pratique)
//     }

//     // 2. Configure le broker de messages
//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry registry) {
//         // préfixes pour les destinations côté client :
//         // le client s'abonne sur /topic/quelque-chose ou /queue/...
//         registry.enableSimpleBroker("/topic", "/queue");

//         // préfixe pour les messages envoyés par le client
//         // le client envoie sur /app/xxx -> mappé vers @MessageMapping("/xxx")
//         registry.setApplicationDestinationPrefixes("/app");
//     }
// }
