package com.alececco.y.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.alececco.y.models.Post;

@Repository
public class PostRepositoryImpl implements PostRepository {

    @Override
    public Optional<Post> findById(Long id) {
        
    }

    @Override
    public List<Post> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public void save(Post post) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }
    

}
