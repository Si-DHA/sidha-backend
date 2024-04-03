package com.sidha.api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sidha.api.service.StorageService;
import com.sidha.api.service.UserService;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;
import java.util.UUID;

@RestController
@RequestMapping("/api/image")
public class ImageController {

  @Autowired
  private StorageService storageService;

  @Autowired
  private UserService userService;

  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] imageData = storageService.getImageFromFileSystem(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }

  @GetMapping("/file/{userId}")
  public ResponseEntity<?> downloadImageFromFileSystemByUserId(@PathVariable String userId) throws IOException {
    try {
      var userUUID = UUID.fromString(userId);
      var user = userService.findById(userUUID);
      var fileName = user.getImageData().getName();
      byte[] imageData = storageService.getImageFromFileSystem(fileName);
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf("image/png"))
          .body(imageData);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(e.getMessage());
    }

  }
}
