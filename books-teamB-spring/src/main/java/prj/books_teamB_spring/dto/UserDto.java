package prj.books_teamB_spring.dto;

import java.time.LocalDateTime;

import lombok.Data;
import prj.books_teamB_spring.model.Role;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private Integer followersCount;
    private Integer followingCount;
    private LocalDateTime lastActivityAt;

    public UserDto(Long id, String username, String email, Role role, Integer followersCount, Integer followingCount,
            LocalDateTime lastActivityAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.lastActivityAt = lastActivityAt;
    }
}
