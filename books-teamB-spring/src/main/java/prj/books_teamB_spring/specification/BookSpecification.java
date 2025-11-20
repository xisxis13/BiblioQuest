package prj.books_teamB_spring.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import prj.books_teamB_spring.dto.BookSearchFilters;
import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.model.Book;
import prj.books_teamB_spring.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> withFilters(BookSearchFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre par titre
            if (filters.getTitle() != null && !filters.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + filters.getTitle().toLowerCase() + "%"
                ));
            }

            // Filtre par auteur - CORRECTION ICI
            if (filters.getAuthor() != null && !filters.getAuthor().trim().isEmpty()) {
                Join<Book, Author> authorJoin = root.join("authors", JoinType.LEFT);
                
                // Chercher dans firstName OU lastName
                Predicate firstNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(authorJoin.get("firstName")),
                    "%" + filters.getAuthor().toLowerCase() + "%"
                );
                
                Predicate lastNameMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(authorJoin.get("lastName")),
                    "%" + filters.getAuthor().toLowerCase() + "%"
                );
                
                predicates.add(criteriaBuilder.or(firstNameMatch, lastNameMatch));
            }

            // Filtre par sujet - CORRECTION ICI AUSSI
            if (filters.getSubject() != null && !filters.getSubject().trim().isEmpty()) {
                Join<Book, Subject> subjectJoin = root.join("subject", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(subjectJoin.get("text")), // "text" au lieu de "name"
                    "%" + filters.getSubject().toLowerCase() + "%"
                ));
            }

            // Filtre par année
            if (filters.getYear() != null) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.function("YEAR", Integer.class, root.get("created_at")),
                    filters.getYear()
                ));
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
