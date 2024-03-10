package com.sidha.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.service.KontrakService;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/kontrak")
public class KontrakController {

  @Autowired
  private KontrakService service;

  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] kontrak = service.getDocumentFromFileSystem(fileName);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
    headers.setContentLength(kontrak.length);
    return new ResponseEntity<>(kontrak, headers, HttpStatus.OK);
  }

  @PostMapping("/{userId}")
  public ResponseEntity<?> uploadDocumentAndSaveToDB(@PathVariable String userId,
      @RequestParam("file") MultipartFile file) throws IOException {
    try {
      UUID userUUID = UUID.fromString(userId);
      service.uploadDocumentAndSaveToDB(file, userUUID);
      return new ResponseEntity<>(new BaseResponse<>(true, 200, "Document uploaded successfully", null), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new BaseResponse<>(false, 500, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
