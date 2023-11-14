package com.example.spring_cookingblog.Repositories;

import com.example.spring_cookingblog.Models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByLoginAndPassword(String login, String password);
}
