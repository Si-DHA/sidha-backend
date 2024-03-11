package com.sidha.api.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.sidha.api.model.ImageData;
import com.sidha.api.model.UserModel;

public interface StorageService {
  public String uploadImageToFileSystem(MultipartFile file, UserModel user) throws IOException;

  public ImageData uploadImageAndSaveToDB(MultipartFile file, UserModel user) throws IOException;

  public byte[] getImageFromFileSystem(String filename) throws IOException;

  public ImageData updateImagaData(MultipartFile file, UserModel user) throws IOException;
}
