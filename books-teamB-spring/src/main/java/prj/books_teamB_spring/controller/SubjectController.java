package prj.books_teamB_spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.Subject;
import prj.books_teamB_spring.repository.SubjectRepository;


@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAllAuthors() {
        return subjectRepository.findAll(); 
    }
}
