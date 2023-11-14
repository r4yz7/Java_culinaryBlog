package com.example.spring_cookingblog.Repositories;

import com.example.spring_cookingblog.Models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post,Long> {
}
