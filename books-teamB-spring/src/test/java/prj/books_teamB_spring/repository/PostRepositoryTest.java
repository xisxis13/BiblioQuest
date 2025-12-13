package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import prj.books_teamB_spring.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private Post testPost;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        testPost = new Post();
        testPost.setContent("Test post content");
        testPost.setUserId(1L);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost = postRepository.save(testPost);
    }

    @Test
    void save_shouldPersistPost() {
        Post newPost = new Post();
        newPost.setContent("New post content");
        newPost.setUserId(2L);
        newPost.setCreatedAt(LocalDateTime.now());

        Post saved = postRepository.save(newPost);

        assertThat(saved.getId()).isNotNull();
        assertThat(postRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnPost_whenPostExists() {
        Optional<Post> found = postRepository.findById(testPost.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Test post content");
    }

    @Test
    void findById_shouldReturnEmpty_whenPostDoesNotExist() {
        Optional<Post> found = postRepository.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnUserPosts() {
        Post post2 = new Post();
        post2.setContent("Another post");
        post2.setUserId(1L);
        post2.setCreatedAt(LocalDateTime.now());
        postRepository.save(post2);

        Post post3 = new Post();
        post3.setContent("Different user post");
        post3.setUserId(2L);
        post3.setCreatedAt(LocalDateTime.now());
        postRepository.save(post3);

        List<Post> userPosts = postRepository.findByUserId(1L);

        assertThat(userPosts).hasSize(2);
        assertThat(userPosts).allMatch(p -> p.getUserId().equals(1L));
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoPosts() {
        List<Post> userPosts = postRepository.findByUserId(999L);

        assertThat(userPosts).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllPosts() {
        Post post2 = new Post();
        post2.setContent("Second post");
        post2.setUserId(2L);
        post2.setCreatedAt(LocalDateTime.now());
        postRepository.save(post2);

        List<Post> allPosts = postRepository.findAll();

        assertThat(allPosts).hasSize(2);
    }

    @Test
    void deleteById_shouldRemovePost() {
        postRepository.deleteById(testPost.getId());

        assertThat(postRepository.findById(testPost.getId())).isEmpty();
    }
}
