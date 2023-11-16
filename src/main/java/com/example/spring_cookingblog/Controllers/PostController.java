package com.example.spring_cookingblog.Controllers;

import com.example.spring_cookingblog.Models.Post;
import com.example.spring_cookingblog.Models.User;
import com.example.spring_cookingblog.Repositories.PostRepository;
import com.example.spring_cookingblog.Repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @GetMapping("/")
    public String getPosts(Model model){
        Iterable< Post>posts = postRepository.findAll();
        model.addAttribute("posts",posts);

        return "post";
    }
    @GetMapping("/post/new")
    public String newPost(Model model, HttpSession session){
        String checkRole = (String)session.getAttribute("userRole");
        if (checkRole.equals("ruser")||checkRole.equals("radmin")){
            return "newPost";
        }
        return "accessDenied";
    }

    @PostMapping("/post/new")
    public String newPost(@RequestParam String title, @RequestParam String context, @RequestParam MultipartFile image, Model model, HttpSession session){
        Post post = new Post();
        post.setContext(context);
        post.setTitle(title);
        post.setAvgRating(0);
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        if(user!=null)
            post.setUser(user);
        try {
            String filename =image.getOriginalFilename();
            String filePath = "src/main/resources/static/" + filename;
            try(var write = new FileOutputStream(filePath)) {
                write.write(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            post.setImageUrl(filename);
            postRepository.save(post);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

    @PostMapping("/post/{id}/details")
    public String getDetails(@PathVariable(value = "id")Long id, Model model){
        Post post = postRepository.findById(id).orElse(null);
        if(post!=null){
            model.addAttribute("post",post);
            return "details";
        }
        return "accessDenied";
    }
}
