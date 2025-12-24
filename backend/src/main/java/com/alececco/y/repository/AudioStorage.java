package com.alececco.y.repository;

public interface AudioStorage {
    
    void store(String filename, byte[] data);

    byte[] load(String filename);

    void delete(String filename);
}
