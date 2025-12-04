package prj.books_teamB_spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import prj.books_teamB_spring.model.Post;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.PostRepository;
import prj.books_teamB_spring.repository.UserRepository;
import prj.books_teamB_spring.service.PostService;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User(
            "testuser",
            "test@example.com",
            "password",
            Role.USER
        );
        testUser = userRepository.save(testUser);
    }

    @Test
    void addPost_createsPostSuccessfully() {
        // Arrange
        Post post = new Post();
        post.setContent("Ceci est un test de publication");
        post.setUserId(testUser.getId());

        // Act
        postService.addPost(post);

        // Assert
        assertThat(post.getId()).isGreaterThan(0);
        assertThat(post.getCreatedAt()).isNotNull();
        assertThat(post.getContent()).isEqualTo("Ceci est un test de publication");
    }

    @Test
    void getAllPosts_returnsAllPosts() {
        // Arrange
        Post post1 = new Post();
        post1.setContent("Premier post");
        post1.setUserId(testUser.getId());
        postService.addPost(post1);

        Post post2 = new Post();
        post2.setContent("Deuxième post");
        post2.setUserId(testUser.getId());
        postService.addPost(post2);

        // Act
        var posts = postService.getAllPosts();

        // Assert
        assertThat(posts).hasSize(2);
    }

    @Test
    void getPostsByUserId_returnsUserPosts() {
        // Arrange
        Post post = new Post();
        post.setContent("Post de l'utilisateur");
        post.setUserId(testUser.getId());
        postService.addPost(post);

        // Act
        var posts = postService.getPostsByUserId(testUser.getId());

        // Assert
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    void deletePost_removesPost() {
        // Arrange
        Post post = new Post();
        post.setContent("Post à supprimer");
        post.setUserId(testUser.getId());
        postService.addPost(post);
        long postId = post.getId();

        // Act
        postService.deletePost(postId);

        // Assert
        assertThat(postRepository.findById(postId)).isEmpty();
    }
}