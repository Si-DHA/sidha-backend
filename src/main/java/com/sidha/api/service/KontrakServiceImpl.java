package com.sidha.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.Kontrak;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.KontrakDb;

@Service
public class KontrakServiceImpl implements KontrakService {
  
  private final String FOLDER_PATH = "/home/nur_fajar11/filedata/";

  @Autowired
  private KontrakDb kontrakDb;

  @Autowired
  private UserService userService;

  @Override
  public String uploadDocumentToFilesystem(MultipartFile file, UserModel user) throws IOException {
    String filePath = FOLDER_PATH + file.getOriginalFilename();

    Kontrak imageData = kontrakDb.save(Kontrak.builder()
        .name(file.getOriginalFilename())
        .type(file.getContentType())
        .user(user)
        .filePath(filePath).build());

    file.transferTo(new File(filePath));

    if (imageData != null) {
      return "Contract\'s document uploaded successfully : " + filePath;
    }
    return null;
  }

  @Override
  public byte[] getDocumentFromFileSystem(String fileName) throws IOException {
    Optional<Kontrak> kontrak = kontrakDb.findByName(fileName);
    String filePath = kontrak.get().getFilePath();
    byte[] kontrakDoc = Files.readAllBytes(new File(filePath).toPath());
    return kontrakDoc;
  }

  @Override
  public Kontrak uploadDocumentAndSaveToDB(MultipartFile file, UUID id) throws IOException {
    var user = userService.findById(id);
    String updatedFilename = user.getId() + "_" + replaceWhitespaceWithUnderscore(file.getOriginalFilename());
    String filePath = FOLDER_PATH + updatedFilename;

    Kontrak kontrak = new Kontrak();
    kontrak.setName(updatedFilename);
    kontrak.setType(file.getContentType());
    kontrak.setFilePath(filePath);
    kontrak.setUser(user);
    kontrakDb.save(kontrak);

    user.setKontrak(kontrak);
    userService.save(user);

    file.transferTo(new File(filePath));

    return kontrak;

  }

  public String replaceWhitespaceWithUnderscore(String fileName) {
    return fileName.replaceAll("\\s", "_");
  }

  @Override
  public Kontrak updateDocumentFile(MultipartFile file, UUID uuid) throws IOException {
    UserModel user = userService.findById(uuid);
    String updatedFilename = user.getId() + "_" + replaceWhitespaceWithUnderscore(file.getOriginalFilename());
    String filePath = FOLDER_PATH + updatedFilename;

    Kontrak kontrak = kontrakDb.findByUser(user).orElse(null);
    kontrak.setName(updatedFilename);
    kontrak.setFilePath(filePath);

    file.transferTo(new File(filePath));

    return kontrak;

  }

}
