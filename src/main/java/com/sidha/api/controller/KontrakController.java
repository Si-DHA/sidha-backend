package com.sidha.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.response.BaseResponse;
import com.sidha.api.service.KontrakService;
import com.sidha.api.service.UserService;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.MediaType;

@RestController
// @CrossOrigin(origins = "*")
@RequestMapping("/api/kontrak")
public class KontrakController {

  @Autowired
  private KontrakService service;

  @Autowired
  private UserService userService;

  @GetMapping("/detail/{userId}")
  public ResponseEntity<?> getMethodName(@PathVariable String userId) {
    try {
      UUID userUUID = UUID.fromString(userId);
      var kontrak = service.getDetailKontrak(userUUID);
      return new ResponseEntity<>(new BaseResponse<>(true, 200, "Detail kontrak", kontrak), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new BaseResponse<>(false, 500, e.getMessage(), null),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/doc/{userId}")
  public ResponseEntity<?> getDocumentByUserId(@PathVariable String userId) {
    try {
      UUID userUUID = UUID.fromString(userId);
      var user = userService.findById(userUUID);
      var userKontrak = user.getKontrak();

      byte[] kontrak = service.getDocumentFromFileSystem(userKontrak.getName());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDisposition(ContentDisposition.builder("inline").filename("kontrak.pdf").build());
      headers.setContentLength(kontrak.length);
      return new ResponseEntity<>(kontrak, headers, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new BaseResponse<>(false, 500, e.getMessage(), null),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/doc/{userId}/view")
  public ResponseEntity<?> viewDocumentByUserId(@PathVariable String userId) throws IOException {

    UUID userUUID = UUID.fromString(userId);
    var user = userService.findById(userUUID);
    var userKontrak = user.getKontrak();

    byte[] kontrak = service.getDocumentFromFileSystem(userKontrak.getName());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentLength(kontrak.length);
    headers.set("X-Frame-Options", "disable");

    ByteArrayResource resource = new ByteArrayResource(kontrak);

    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(kontrak.length)
        .body(kontrak);
  }

  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] kontrak = service.getDocumentFromFileSystem(fileName);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
    headers.setContentLength(kontrak.length);
    ByteArrayResource resource = new ByteArrayResource(kontrak);
    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  }

  @PostMapping("/{userId}")
  public ResponseEntity<?> uploadDocumentAndSaveToDB(@PathVariable String userId,
      @RequestPart("file") MultipartFile file) throws IOException {
    try {
      UUID userUUID = UUID.fromString(userId);
      service.uploadDocumentAndSaveToDB(file, userUUID);
      return new ResponseEntity<>(new BaseResponse<>(true, 200, "Dokumen kontrak berhasil diunggah", null), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(new BaseResponse<>(false, 500, e.getMessage(), null),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getMethodName() {
    var listKontrak = service.getAllClientContract();
    if (listKontrak.size() > 0) {
      return new ResponseEntity<>(new BaseResponse<>(true, 200, "Daftar kontrak", listKontrak), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new BaseResponse<>(true, 200, "Tidak ada data kontrak", null), HttpStatus.NOT_FOUND);
    }
  }

}
