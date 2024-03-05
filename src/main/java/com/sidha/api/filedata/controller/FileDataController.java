package com.sidha.api.filedata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.filedata.services.StorageService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class FileDataController {

  private final StorageService service;

  @PostMapping
  public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
    String uploadImage = service.uploadImage(file);
    return ResponseEntity.status(HttpStatus.OK)
        .body(uploadImage);
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
    byte[] imageData = service.downloadImage(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }

  @PostMapping("/fileSystem")
  public ResponseEntity<?> uploadImageToFIleSystem(@RequestParam("image") MultipartFile file) throws IOException {
    String uploadImage = service.uploadImageToFileSystem(file);
    return ResponseEntity.status(HttpStatus.OK)
        .body(uploadImage);
  }

  @GetMapping("/fileSystem/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] imageData = service.downloadImageFromFileSystem(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }

}
