package com.example.spring_cookingblog.Controllers;

import com.example.spring_cookingblog.Models.Role;
import com.example.spring_cookingblog.Models.User;
import com.example.spring_cookingblog.Repositories.RoleRepository;
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

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @GetMapping("/user/register")
    public String registerUser(Model model){
        return "registerUser";
    }

    @PostMapping("/user/register")
    public String registerUser(@RequestParam String login, @RequestParam String email, @RequestParam String password,
                               Model model){
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(password);
        Role role = roleRepository.findById(1L).orElse(null);
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/user/login")
    public String loginUser(Model model){
        return "loginUser";
    }

    @PostMapping("/user/login")
    public String loginUser(@RequestParam String login, @RequestParam String password, Model model, HttpSession session){
        User user = userRepository.findByLoginAndPassword(login,password);
        if(user!=null){
            Role role = user.getRole();
            if(role.getId()==1){
                session.setAttribute("userRole","ruser");
            }
            else if(role.getId()==2){
                session.setAttribute("userRole","radmin");
            }
            return "redirect:/";
        }
        return "loginUser";
    }

    @GetMapping("/user/exit")
    public String exitUser(Model model, HttpSession session){
       session.invalidate();
       return "redirect:/user/register";
    }

    @GetMapping("/users")
    public String getUsers(Model model){
        Iterable<User>users = userRepository.findAll();
        model.addAttribute("users",users);
        return "users";
    }

    @GetMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable(value = "id")Long id,Model model){
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            userRepository.delete(user);
        }
        return "users";
    }

    @GetMapping("/user/{id}/edit")
    public String editUser(@PathVariable(value = "id")Long id,Model model){
        User user = userRepository.findById(id).orElse(null);
        if (user!=null){
            model.addAttribute("user",user);
            Iterable<Role> roles = roleRepository.findAll();
            model.addAttribute("roles",roles);
            return "editUser";
        }
        return "users";
    }

    @PostMapping("/user/{id}/edit")
    public String editUser(@PathVariable(value = "id")Long id,@RequestParam String login, @RequestParam String email, @RequestParam Long roleId,Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (!login.equals(user.getLogin())) {
                user.setLogin(login);
            }
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null) {
                user.setRole(role);
            }
            userRepository.save(user);
        }
        return "redirect:/users";
    }
}
