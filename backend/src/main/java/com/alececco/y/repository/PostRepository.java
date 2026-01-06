package com.alececco.y.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alececco.y.models.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
