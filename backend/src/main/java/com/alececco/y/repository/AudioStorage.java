package com.alececco.y.repository;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public interface AudioStorage {

    void store(String key, InputStream data);

    Resource load(String key);

    void delete(String key);
}
