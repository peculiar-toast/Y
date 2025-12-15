package com.alececco.y.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AudioFile {
    
    private String filename;
    private byte[] data;
}
