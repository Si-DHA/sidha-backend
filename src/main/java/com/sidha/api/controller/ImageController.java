package com.sidha.api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.service.StorageService;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/image")
public class ImageController {

  @Autowired
  private StorageService service;

  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] imageData = service.getImageFromFileSystem(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }
}
