package com.alececco.y.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.models.AudioFile;
import com.alececco.y.service.AudioStorageService;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
// @RequestMapping("/api/audio")
@RequestMapping("/public/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioStorageService audioStorageService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }


        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }

        AudioFile audioFile = AudioFile.builder()
                .filename(file.getOriginalFilename())
                .data(fileData)
                .build();

        try {
            audioStorageService.store(audioFile);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
        return ResponseEntity.ok("File stored at: " + audioFile.getFilename());
    }

}
