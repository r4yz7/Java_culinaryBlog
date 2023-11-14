package com.example.spring_cookingblog.Repositories;

import com.example.spring_cookingblog.Models.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment,Long> {
}
