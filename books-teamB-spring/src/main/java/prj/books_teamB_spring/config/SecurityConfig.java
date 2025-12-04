package prj.books_teamB_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  
                .requestMatchers("/api/catalog/**").permitAll()
                .requestMatchers("/api/books/**").permitAll()
                .requestMatchers("/api/posts/**").permitAll()
                .requestMatchers("/api/books/add").hasRole("AUTEUR")  
                .requestMatchers("/api/books/${id}/edit").hasRole("BIBLIOTHECAIRE")
                .anyRequest().denyAll()
            )
            .httpBasic(basic -> {});  

        return http.build();
    }

}