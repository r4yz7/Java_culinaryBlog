package com.example.spring_cookingblog.Repositories;

import com.example.spring_cookingblog.Models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post,Long> {

    List<Post> findByTitleContainingIgnoreCase(String title);
}
