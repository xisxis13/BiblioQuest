package prj.books_teamB_spring.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.BookRepository;
import prj.books_teamB_spring.repository.UserRepository;

//Create a default Admin user and import sample books.csv for dev
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

    @Bean
    CommandLineRunner importSampleBooks(BookRepository bookRepository) {
        return args -> {
            try {
                ClassPathResource r = new ClassPathResource("books.csv");
                if (!r.exists()) return;
                InputStream is = r.getInputStream();
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                List<List<String>> rows = parseCsv(content);
                if (rows.size() <= 1) return;
                for (int i = 1; i < rows.size(); i++) {
                    List<String> cols = rows.get(i);
                    if (cols.size() < 2) continue;
                    String externalId = cols.get(0).trim();
                    String title = cols.get(1).trim();
                    String desc = cols.size() > 2 ? cols.get(2).trim() : null;
                    String cover = cols.size() > 3 ? cols.get(3).trim() : null;
                    Book b = new Book();
                    b.setSerie_id(externalId);
                    b.setTitle(title);
                    b.setDesc_txt(desc);
                    b.setCover(cover);
                    b.setCreated_at(LocalDateTime.now());
                    b.setUpdated_at(LocalDateTime.now());
                    boolean exists = false;
                    for (Book existing : bookRepository.findAll()) {
                        if (externalId != null && externalId.equals(existing.getSerie_id())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) bookRepository.save(b);
                }
            } catch (Exception e) {
                System.err.println("DataInitializer import error: " + e.getMessage());
            }
        };
    }

    // Minimal CSV parser that handles quoted fields and newlines inside quotes
    private List<List<String>> parseCsv(String content) {
        List<List<String>> rows = new ArrayList<>();
        List<String> currentRow = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                    field.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                currentRow.add(field.toString());
                field.setLength(0);
            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                if (c == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    i++;
                }
                currentRow.add(field.toString());
                field.setLength(0);
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            } else {
                field.append(c);
            }
        }
        if (field.length() > 0 || !currentRow.isEmpty()) {
            currentRow.add(field.toString());
            rows.add(currentRow);
        }
        return rows;
    }
}