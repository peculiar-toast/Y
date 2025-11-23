package com.alececco.y.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
//@RequestMapping("/api/audio")
@RequestMapping("/public/audio")
@RequiredArgsConstructor
public class AudioController {
    
    @PostMapping("/upload", consumes="multipart/form-data")
    public String uploadAudio(@RequestParam("file") MultipartFile file) {
        // Logica per gestire il caricamento del file audio
        return "File audio caricato con successo: " + file.getOriginalFilename();
    }

}
