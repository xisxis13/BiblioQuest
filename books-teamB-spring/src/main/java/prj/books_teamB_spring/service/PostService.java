package prj.books_teamB_spring.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import prj.books_teamB_spring.model.Post;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Post addPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        return postRepository.findByUserId(userId);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
    }

    public void deletePost(Long postId) {
        if (!postRepository.findById(postId).isPresent()) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }
}
