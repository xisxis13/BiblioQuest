package prj.books_teamB_spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import prj.books_teamB_spring.dto.PostDTO;
import prj.books_teamB_spring.model.Post;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.PostService;
import prj.books_teamB_spring.service.UserService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PostController {

    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // POST /api/posts → crée un post et renvoie l'objet créé
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody Post post) {
        
        postService.addPost(post);
        User user = this.userService.findById(post.getUserId());
        PostDTO postDTO = new PostDTO(
            post.getUserId(),
            user.getUsername(),
            post.getContent(),
            post.getBookId(),
            post.getCreatedAt()
        );
        return new ResponseEntity<>(postDTO, HttpStatus.CREATED);
    }

    // GET /api/posts/all → version simple pour debug
    @GetMapping("/all")
    public List<PostDTO> getAll() {
        List<Post> posts = postService.getAllPosts();
        List<PostDTO> postDTOs = new ArrayList<>();

        for(Post post: posts){
            User user = this.userService.findById(post.getUserId());
            postDTOs.add(new PostDTO(
                post.getUserId(),
                user.getUsername(),
                post.getContent(),
                post.getBookId(),
                post.getCreatedAt()
            ));
        }
        return postDTOs;
    }

    // DELETE /api/posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/{id}")
    public List<PostDTO> getUserPosts(
        @PathVariable Long id
    ) {
        List<Post> posts = postService.getPostsByUserId(id);
        List<PostDTO> postDTOs = new ArrayList<>();

        User user = this.userService.findById(id);
        for(Post post: posts){
            postDTOs.add(new PostDTO(
                post.getUserId(),
                user.getUsername(),
                post.getContent(),
                post.getBookId(),
                post.getCreatedAt()
            ));
        }
        return postDTOs;
    }
}
