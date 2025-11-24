package prj.books_teamB_spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.model.Serie;
import prj.books_teamB_spring.repository.SerieRepository;


@RestController
@RequestMapping("/api/serie")
@RequiredArgsConstructor
public class SerieController {

    private final SerieRepository serieRepository;

    @GetMapping
    public List<Serie> getAllSeries() {
        return serieRepository.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Serie> getSeriekById(@PathVariable Long id) {
        return serieRepository.findById(id)
                .map(ResponseEntity::ok)                
                .orElseGet(() -> ResponseEntity.notFound().build()); 
    }
}
