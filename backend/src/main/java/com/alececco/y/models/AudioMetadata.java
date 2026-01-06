package com.alececco.y.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class AudioMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String storageKey;
    private String contentType;
    private long size;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
