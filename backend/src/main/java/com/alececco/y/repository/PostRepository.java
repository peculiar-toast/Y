package com.alececco.y.repository;

import java.util.List;
import java.util.Optional;

import com.alececco.y.models.Posts;

public interface PostRepository {
    
    Optional<Posts> findById(Long id);

    List<Posts> findAll();

    void save(Posts post);

    void deleteById(Long id);
}
