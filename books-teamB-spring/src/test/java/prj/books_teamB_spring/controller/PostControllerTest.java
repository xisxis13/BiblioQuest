package prj.books_teamB_spring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import prj.books_teamB_spring.exception.GlobalExceptionHandler;
import prj.books_teamB_spring.model.Post;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.service.PostService;
import prj.books_teamB_spring.service.UserService;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;

    private User user1;
    private Post post1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        user1 = new User("user1", "user1@example.com", "password", Role.USER);
        user1.setId(1L);

        post1 = new Post();
        post1.setId(1L);
        post1.setUserId(1L);
        post1.setContent("This is a post");
        post1.setBookId(123L); // Using Long
        post1.setCreatedAt(LocalDateTime.now()); // Using LocalDateTime
    }

    @Test
    void createPost_shouldReturnCreatedPostDTO() throws Exception {
        when(userService.findById(1L)).thenReturn(user1);
        when(postService.addPost(any(Post.class))).thenReturn(post1);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.content", is("This is a post")));
    }

    @Test
    void getAll_shouldReturnListOfPostDTOs() throws Exception {
        Post post2 = new Post();
        post2.setId(2L);
        post2.setUserId(1L);
        post2.setContent("This is another post");
        post2.setBookId(456L); // Using Long
        post2.setCreatedAt(LocalDateTime.now()); // Using LocalDateTime

        when(postService.getAllPosts()).thenReturn(List.of(post1, post2));
        when(userService.findById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/posts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is("This is a post")))
                .andExpect(jsonPath("$[1].content", is("This is another post")));
    }

    @Test
    void deletePost_shouldReturnNoContent() throws Exception {
        doNothing().when(postService).deletePost(anyLong());

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserPosts_shouldReturnListOfUserPostDTOs() throws Exception {
        when(postService.getPostsByUserId(1L)).thenReturn(List.of(post1));
        when(userService.findById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[0].content", is("This is a post")));
    }
}
