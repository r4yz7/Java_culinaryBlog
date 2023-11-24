package com.example.spring_cookingblog.Controllers;

import com.example.spring_cookingblog.Models.Comment;
import com.example.spring_cookingblog.Models.Post;
import com.example.spring_cookingblog.Models.User;
import com.example.spring_cookingblog.Repositories.CommentRepository;
import com.example.spring_cookingblog.Repositories.PostRepository;
import com.example.spring_cookingblog.Repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
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
import java.util.List;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;
    @GetMapping("/")
    public String getPosts(Model model,HttpSession session){
        Iterable< Post>posts = postRepository.findAll();
        model.addAttribute("posts",posts);
        model.addAttribute("session",session);
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

    @GetMapping("/post/{id}/comment")
    public String sendComment(@PathVariable(value = "id")Long id,HttpSession session, Model model){
        Long userId = (long) session.getAttribute("userId");
        if(userId!=null){
            Post post = postRepository.findById(id).orElse(null);
            model.addAttribute("post", post);
            return "sendComment";
        }
        return "redirect:/user/login";
    }
    @PostMapping("/post/{id}/comment")
    public String sendComment(@PathVariable(value = "id")Long id,@RequestParam String text, @RequestParam int rating, HttpSession session, Model model){
        Post post = postRepository.findById(id).orElse(null);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setRating(rating);
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        if(user!=null)
            comment.setUser(user);
        if(post!=null)
        {
            comment.setPost(post);
            calcRating(post,rating);
            postRepository.save(post);
        }
        commentRepository.save(comment);
        return "redirect:/";
    }

    private void calcRating(Post post, int rating) {
        List<Comment> comments = post.getComments();
        if (comments != null && !comments.isEmpty()) {
            int totalRating = 0;
            for (Comment comment : comments) {
                totalRating += comment.getRating();
            }
            int avgRating = (totalRating + rating) / (comments.size() + 1);
            post.setAvgRating(avgRating);
        } else {
            post.setAvgRating(rating);
        }
    }

    @GetMapping("/post/{id}/edit")
    private String postEdit(@PathVariable(value = "id")Long id, Model model, HttpSession session){
        String userRole = (String) session.getAttribute("userRole");
        Long userId = (Long) session.getAttribute("userId");
        Post post = postRepository.findById(id).orElse(null);
        if(userRole=="radmin" || userId == post.getUser().getId()){
            if(post!=null){
                model.addAttribute("post",post);
                return "postEdit";
            }
            return "redirect:/";
        }
        return "accessDenied";
    }

    @GetMapping("/post/{id}/delete")
    private String postDelete(@PathVariable(value = "id") Long id, Model model,HttpSession session){
        String userRole = (String) session.getAttribute("userRole");
        Long userId = (Long) session.getAttribute("userId");
        Post post = postRepository.findById(id).orElse(null);
        if(userRole=="radmin" || userId == post.getUser().getId()){
            if(post!=null){
                postRepository.delete(post);
            }
            return "redirect:/";
        }
        return "accessDenied";
    }

    @PostMapping("/postSearch")
    private String postSearch(@RequestParam String title, Model model){
    List<Post> postsSearch = postRepository.findByTitleContainingIgnoreCase(title);
    if(postsSearch!=null){
        model.addAttribute("posts",postsSearch);
        return "post";
    }
    return "redirect:/";
    }

    @PostMapping("/post/{id}/edit")
    private String postEdit(@PathVariable(value = "id")Long id, @RequestParam String title, @RequestParam String text, Model model){
        Post post = postRepository.findById(id).orElse(null);
        if (post!=null){
            if(!title.equals(post.getTitle()))
                post.setTitle(title);
            if(!text.equals(post.getContext()))
                post.setContext(text);
//            if(!img.getOriginalFilename().equals(post.getImageUrl())){
//                try {
//                    String filename = img.getOriginalFilename();
//                    String filePath = "src/main/resources/static/" + filename;
//                    try(var write = new FileOutputStream(filePath)) {
//                        write.write(img.getBytes());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    post.setImageUrl(filename);
//                } catch (RuntimeException e) {
//                    throw new RuntimeException(e);
//                }
//            }
            postRepository.save(post);
        }
        return "redirect:/";
    }


}
