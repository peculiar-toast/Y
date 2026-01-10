package com.alececco.y.testfactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Used in testing. Generates MockMultipartFiles with a progressive id
 */
public final class MultipartFileTestFactory {

    private MultipartFileTestFactory() {
    }

    public static MultipartFile single(String name) {
        return new MockMultipartFile(name, name + ".mp3", "audio/mpeg",
                ("content of " + name).getBytes(StandardCharsets.UTF_8));
    }

    public static List<MultipartFile> multiple(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> single("file" + i))
                .toList();
    }

    public static MultipartFile custom(String name, String filename, String contentType, byte[] content) {
        return new MockMultipartFile(name, filename, contentType, content);
    }
}
