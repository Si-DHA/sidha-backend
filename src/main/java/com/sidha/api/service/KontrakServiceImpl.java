package com.sidha.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.DTO.response.KontrakKlienResponse;
import com.sidha.api.DTO.response.KontrakResponse;
import com.sidha.api.model.Kontrak;
import com.sidha.api.model.enumerator.Role;
import com.sidha.api.model.user.Klien;
import com.sidha.api.model.user.UserModel;
import com.sidha.api.repository.KontrakDb;
import org.springframework.core.env.Environment;


@Service
public class KontrakServiceImpl implements KontrakService {
  
  @Value("${kontrak.folder.path}")
  private  String FOLDER_PATH;

  @Autowired
  private KontrakDb kontrakDb;

  @Autowired
  private UserService userService;

  @Autowired
  private Environment environment;

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
    if (user == null) {
      throw new RuntimeException("User not found");
    }

    if (user.getRole() != Role.KLIEN) {
      var userRole = user.getRole();
      throw new RuntimeException(userRole + " doesn't have a contract document");
    }
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

    Optional<Kontrak> kontrak = kontrakDb.findByUser(user);
    if (kontrak.isPresent()){
      Kontrak kontrakUser = kontrak.get();
      this.deleteKontrak(kontrakUser);
      kontrakUser.setName(updatedFilename);
      kontrakUser.setFilePath(filePath);
      file.transferTo(new File(filePath));
      kontrakDb.save(kontrakUser);
      return kontrakUser;
    } else{
      Kontrak newKontrak = this.uploadDocumentAndSaveToDB(file, uuid);
      return newKontrak;
    }


  }

  @Override
  public KontrakResponse getDetailKontrak(UUID userId) {
    var user = userService.findById(userId);
    if (user == null) {
      throw new RuntimeException("Akun tidak ditemukan");
    }
    if (user.getRole() != Role.KLIEN) {
      var userRole = user.getRole();
      throw new RuntimeException(userRole + " tidak memiliiki dokumen kontrak");

    }
    var response = new KontrakResponse();
    response.setUser(user);

    var kontrak = user.getKontrak();
    if (kontrak == null) {
      return response;
    }
    response.setKontrakUrl(getUrlPath() + "/kontrak/doc/" + user.getId());
    response.setCreatedAt(kontrak.getCreatedAt());
    response.setUpdatedAt(kontrak.getUpdatedAt());
    return response;

  }

  @Override
  public List<KontrakKlienResponse> getAllClientContract() {
    var listKontrak = kontrakDb.findAllClientContract();
    if (listKontrak.size() == 0 || listKontrak == null) {
      throw new RuntimeException("Tidak ada data kontrak ditemukan");
    }
    List<KontrakKlienResponse> listKontrakResponse = new ArrayList<KontrakKlienResponse>();
    try {
      for (Kontrak kontrak : listKontrak) {
        var user = kontrak.getUser();
        if (user.getRole() == Role.KLIEN) {
          var klien = (Klien) user;
          listKontrakResponse.add(KontrakKlienResponse.builder()
              .userId(klien.getId())
              .nama(klien.getName())
              .companyName(klien.getCompanyName())
              .imageUrl(getUrlPath() + "/image/file/" + klien.getId())
              .email(klien.getEmail())
              .phone(klien.getPhone())
              .kontrakUrl(getUrlPath() + "/kontrak/doc/" + klien.getId())
              .createdAt(kontrak.getCreatedAt())
              .updatedAt(kontrak.getUpdatedAt())
              .build());
        }
      }
      return listKontrakResponse;

    } catch (Exception e) {
      throw new RuntimeException("Terjadi error : ", e);
    }

  }
public void deleteKontrak(Kontrak kontrak){
  if (kontrak != null) {
    File fileToDelete = new File(kontrak.getFilePath());
    fileToDelete.delete();
  }
}

  public String getUrlPath() {
    if (FOLDER_PATH.contains("devinahaz")) {
      return "https://sidha-backend.site/api";
    } else {
      var port = environment.getRequiredProperty("server.port");
      return "http://localhost:" + port + "/api";
    }
  }

}