package com.sidha.api.filedata.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.filedata.model.FileData;
import com.sidha.api.filedata.model.ImageData;
import com.sidha.api.filedata.repository.FileDataRepository;
import com.sidha.api.filedata.repository.StorageRepository;
import com.sidha.api.filedata.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class StorageService {

  @Autowired
  private StorageRepository repository;

  @Autowired
  private FileDataRepository fileDataRepository;

  private final String FOLDER_PATH = "C:\\Users\\User\\Desktop\\filedata\\";

  public String uploadImage(MultipartFile file) throws IOException {
    ImageData imageData = repository.save(ImageData.builder()
        .name(file.getOriginalFilename())
        .type(file.getContentType())
        .imageData(ImageUtils.compressImage(file.getBytes())).build());
    if (imageData != null) {
      return "file uploaded successfully : " + file.getOriginalFilename();
    }
    return null;
  }

  public byte[] downloadImage(String fileName) {
    Optional<ImageData> dbImageData = repository.findByName(fileName);
    byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
    return images;
  }

  public String uploadImageToFileSystem(MultipartFile file) throws IOException {
    String filePath = FOLDER_PATH + file.getOriginalFilename();

    FileData fileData = fileDataRepository.save(FileData.builder()
        .name(file.getOriginalFilename())
        .type(file.getContentType())
        .filePath(filePath).build());

    file.transferTo(new File(filePath));

    if (fileData != null) {
      return "file uploaded successfully : " + filePath;
    }
    return null;
  }

  public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
    Optional<FileData> fileData = fileDataRepository.findByName(fileName);
    String filePath = fileData.get().getFilePath();
    byte[] images = Files.readAllBytes(new File(filePath).toPath());
    return images;
  }

}
