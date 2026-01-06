package com.alececco.y.dto.audio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioDTO {
    private Long id;
    private String url;
}
