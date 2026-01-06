package com.alececco.y.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioFile {

    private String filename;
    private String storageKey;
    private String contentType;
    private long size;
}
