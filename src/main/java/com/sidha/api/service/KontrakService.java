package com.sidha.api.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.Kontrak;
import com.sidha.api.model.user.UserModel;

public interface KontrakService {
  public String uploadDocumentToFilesystem(MultipartFile file, UserModel user) throws IOException;

  public Kontrak uploadDocumentAndSaveToDB(MultipartFile file, UUID userId) throws IOException;

  public byte[] getDocumentFromFileSystem(String filename) throws IOException;

  public Kontrak updateDocumentFile(MultipartFile file, UUID userId) throws IOException;
}
