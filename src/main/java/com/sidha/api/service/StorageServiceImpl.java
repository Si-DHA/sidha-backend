package com.sidha.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.ImageData;
import com.sidha.api.model.UserModel;
import com.sidha.api.repository.ImageDataDb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {

  private final String FOLDER_PATH = "/home/nur_fajar11/imagedata/";

  @Autowired
  private ImageDataDb imageDataRepository;

  @Override
  public String uploadImageToFileSystem(MultipartFile file, UserModel user) throws IOException {
    String filePath = FOLDER_PATH + file.getOriginalFilename();

    ImageData imageData = imageDataRepository.save(ImageData.builder()
        .name(file.getOriginalFilename())
        .type(file.getContentType())
        .user(user)
        .filePath(filePath).build());

    file.transferTo(new File(filePath));

    if (imageData != null) {
      return "image uploaded successfully : " + filePath;
    }
    return null;
  }

  @Override
  public byte[] getImageFromFileSystem(String fileName) throws IOException {
    Optional<ImageData> imageData = imageDataRepository.findByName(fileName);
    String filePath = imageData.get().getFilePath();
    byte[] images = Files.readAllBytes(new File(filePath).toPath());
    return images;
  }

  @Override
  public ImageData uploadImageAndSaveToDB(MultipartFile file, UserModel user) throws IOException {
    String updatedFilename = user.getId() + "_" + replaceWhitespaceWithUnderscore(file.getOriginalFilename());
    String filePath = FOLDER_PATH + updatedFilename;

    ImageData imageData = imageDataRepository.save(ImageData.builder()
        .name(updatedFilename)
        .type(file.getContentType())
        .user(user)
        .filePath(filePath).build());

    file.transferTo(new File(filePath));

    if (imageData != null) {
      return imageData;
    }
    return null;
  }

  public String replaceWhitespaceWithUnderscore(String fileName) {
    return fileName.replaceAll("\\s", "_");
  }

  @Override
  public ImageData updateImagaData(MultipartFile file, UserModel user) throws IOException {
    Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
    String updatedFilename = user.getId() + "_" + replaceWhitespaceWithUnderscore(file.getOriginalFilename());
    String filePath = FOLDER_PATH + updatedFilename;

    ImageData imageData = imageDataRepository.findByUserId(user.getId()).get();
    logger.info("image data : " + imageData);
    if (imageData != null) {
      File fileToDelete = new File(imageData.getFilePath());
      fileToDelete.delete();
      logger.info("image ada");
    }
    if (imageData == null) {
      logger.info("imageNull");
      throw new RuntimeException("Image not found");
    }

    imageData.setName(updatedFilename);
    imageData.setFilePath(filePath);
    imageData.setType(file.getContentType());
    file.transferTo(new File(filePath));

    return imageDataRepository.save(imageData);

  }

}
