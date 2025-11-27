package com.alececco.y.service;

public interface AudioStorageService {
    Path store(MultipartFile file);
    Post findByName(String name);
}
