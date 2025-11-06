package prj.books_teamB_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import prj.books_teamB_spring.repository.UserDB;


@Service
public class UserService {
    @Autowired
    private UserDB userDB;
    
}
