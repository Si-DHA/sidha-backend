package com.sidha.api.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.user.UserModel;

public interface StorageService {
  String uploadImageToFileSystem(MultipartFile file, UserModel user) throws IOException;

  ImageData uploadImageAndSaveToDB(MultipartFile file, String filename) throws IOException;

  ImageData uploadProfile(MultipartFile file, UserModel user) throws IOException;

  byte[] getImageFromFileSystem(String filename) throws IOException;

ImageData updateProfileImage(MultipartFile file, UserModel user) throws IOException;

  ImageData updateImageInDB(MultipartFile file, ImageData imageData, String filename) throws IOException;

  void deleteImageFile(ImageData imageData);

}
