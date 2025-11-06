package prj.books_teamB_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import prj.books_teamB_spring.service.UserService;


@Controller
public class UserController {
    @Autowired
    private UserService userService;
    
}
