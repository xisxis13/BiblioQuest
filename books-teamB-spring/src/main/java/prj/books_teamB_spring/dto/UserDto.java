package prj.books_teamB_spring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private Integer followersCount;
    private Integer followingCount;
    private LocalDateTime lastActivityAt;
    
    public UserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.followersCount = user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
        this.lastActivityAt = user.getLastActivityAt();
    }
}
