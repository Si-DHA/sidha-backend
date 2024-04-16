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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {

  // private final String FOLDER_PATH = "/home/nur_fajar11/imagedata/";
  // private final String FOLDER_PATH = "/Users/devina.hana/Documents/College/term 6/propensi/imagedata/";
  // private final String FOLDER_PATH = "C:\\Users\\USER\\PROPENSI\\imagedata\\";
  private final String FOLDER_PATH = "C:\\Users\\LENOVO\\Downloads\\";

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
      return "Gambar berhasil disimpan : " + filePath;
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
    String filePath = FOLDER_PATH + updatedFilename;
    ImageData imageData = imageDataDb.findProfileByUserId(user.getId()).get();
  
    logger.info("image data : " + imageData);
    if (imageData != null) {
      logger.info("image ada");
      return this.updateImageInDB(file, imageData, updatedFilename);
    } else {
    
      ImageData imageData2 = ImageData.builder()
          .name(updatedFilename)
          .type(file.getContentType())
          .filePath(filePath).build();
  
      modelMapper.map(imageData2, ProfileImage.class).setUser(user);
      file.transferTo(new File(filePath));
      return imageDataDb.save(imageData2);
  
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
