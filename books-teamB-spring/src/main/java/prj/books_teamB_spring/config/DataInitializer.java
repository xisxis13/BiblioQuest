package prj.books_teamB_spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.repository.UserRepository;

//Create a default Admin user
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setPassword("admin123");
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
            }
        };
    }
}