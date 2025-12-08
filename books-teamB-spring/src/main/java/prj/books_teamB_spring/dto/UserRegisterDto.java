package prj.books_teamB_spring.dto;

import lombok.Data;

@Data
public class UserRegisterDto {

    private String username;
    private String email;
    private String password;

    public UserRegisterDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
