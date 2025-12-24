package com.alececco.y.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;

public interface AudioMetadataRepository extends JpaRepository<AudioMetadata, Long> {
    List<AudioMetadata> findByPost(Post post);
}
