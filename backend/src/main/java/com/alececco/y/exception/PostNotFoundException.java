package com.alececco.y.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("Post not found: " + id);
    }
}
