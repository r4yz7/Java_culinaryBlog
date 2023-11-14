package com.example.spring_cookingblog.Controllers;

import com.example.spring_cookingblog.Models.Post;
import com.example.spring_cookingblog.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    @GetMapping("/")
    public String getPosts(Model model){
        Iterable< Post>posts = postRepository.findAll();
        model.addAttribute("posts",posts);
        return "post";
    }
}
