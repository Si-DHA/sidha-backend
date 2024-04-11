package com.sidha.api.service;

import com.sidha.api.model.image.ProfileImage;
import com.sidha.api.model.user.UserModel;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.image.ImageData;
import com.sidha.api.repository.ImageDataDb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {

  @Value("${image.folder.path}")
  private  String FOLDER_PATH;

  @Autowired
  private ImageDataDb imageDataDb;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public String uploadImageToFileSystem(MultipartFile file, UserModel user) throws IOException {
    String filePath = FOLDER_PATH + file.getOriginalFilename();

    ImageData imageData = ImageData.builder()
        .name(file.getOriginalFilename())
        .type(file.getContentType())
//        .user(user)
        .filePath(filePath).build();

    modelMapper.map(imageData, ProfileImage.class).setUser(user);

    imageDataDb.save(imageData);



    file.transferTo(new File(filePath));

    if (imageData != null) {
      return "image uploaded successfully : " + filePath;
    }
    return null;
  }

  @Override
  public byte[] getImageFromFileSystem(String fileName) throws IOException {
    Optional<ImageData> imageData = imageDataDb.findByName(fileName);
    String filePath = imageData.get().getFilePath();
    byte[] images = Files.readAllBytes(new File(filePath).toPath());
    return images;
  }

  @Override
  public ImageData uploadImageAndSaveToDB(MultipartFile file, String filename) throws IOException {
    filename = replaceWhitespaceWithUnderscore(filename);
    String filePath = FOLDER_PATH + filename;

    ImageData imageData = ImageData.builder()
            .name(filename)
            .type(file.getContentType())
            .filePath(filePath).build();

    file.transferTo(new File(filePath));

    return imageData;
  }

  @Override
  public ImageData uploadProfile(MultipartFile file, UserModel user) throws IOException {
    String updatedFilename = user.getId() + "_" + file.getOriginalFilename();
    return this.uploadImageAndSaveToDB(file, updatedFilename);
  }

  public String replaceWhitespaceWithUnderscore(String fileName) {
    return fileName.replaceAll("\\s", "_");
  }

  @Override
  public ImageData updateProfileImage(MultipartFile file, UserModel user) throws IOException {
    Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
    String updatedFilename = user.getId() + "_" + replaceWhitespaceWithUnderscore(file.getOriginalFilename());

    ImageData imageData = imageDataDb.findProfileByUserId(user.getId()).get();
    logger.info("image data : " + imageData);
    if (imageData != null) {
      logger.info("image ada");
      return this.updateImageInDB(file, imageData, updatedFilename);
    } else {
      throw new RuntimeException("Image not found");
    }

  }

  @Override
  public ImageData updateImageInDB(MultipartFile file, ImageData imageData, String filename) throws IOException {
    String filePath = FOLDER_PATH + filename;
    this.deleteImageFile(imageData);

    imageData.setName(filename);
    imageData.setFilePath(filePath);
    imageData.setType(file.getContentType());
    file.transferTo(new File(filePath));

    return imageDataDb.save(imageData);
  }

  @Override
  public void deleteImageFile(ImageData imageData) {
    if (imageData != null) {
      File fileToDelete = new File(imageData.getFilePath());
      fileToDelete.delete();
    }
  }

}
