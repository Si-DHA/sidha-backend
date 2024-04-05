package com.sidha.api.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.UserModel;

public interface StorageService {
  public String uploadImageToFileSystem(MultipartFile file, UserModel user) throws IOException;

  public ImageData uploadImageAndSaveToDB(MultipartFile file, String filename) throws IOException;

  public ImageData uploadProfile(MultipartFile file, UserModel user) throws IOException;

  public byte[] getImageFromFileSystem(String filename) throws IOException;

  String replaceWhitespaceWithUnderscore(String fileName);

  public ImageData updateProfileImage(MultipartFile file, UserModel user) throws IOException;
  public ImageData updateImageInDB(MultipartFile file, ImageData imageData, String filename) throws IOException;
}
