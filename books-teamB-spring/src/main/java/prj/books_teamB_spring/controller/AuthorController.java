package prj.books_teamB_spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.Author;
import prj.books_teamB_spring.repository.AuthorRepository;


@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorRepository authorRepository;

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorRepository.findAll(); 
    }
}
