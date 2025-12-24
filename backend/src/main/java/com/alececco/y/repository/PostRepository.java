package com.alececco.y.repository;

import java.util.List;
import java.util.Optional;

import com.alececco.y.models.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    Optional<Post> findById(Long id);

    List<Post> findAll();

    void save(Post post);

    void deleteById(Long id);
}
