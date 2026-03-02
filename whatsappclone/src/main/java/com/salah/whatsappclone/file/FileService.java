package com.salah.whatsappclone.file;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    public String SaveFile(@NonNull MultipartFile sourceFile,
                           @NonNull String senderId) {

        final String fileUploadSubPath = "users" + separator + senderId;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile ,
                              @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(fileUploadPath);
        if (!targetFolder.exists()) {
            boolean folderscreated = targetFolder.mkdirs();
            if (!folderscreated) {
                log.error("Unable to create folder"+fileUploadPath);
            }
        }

        final String fileExtention= getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = fileUploadPath + separator + currentTimeMillis()+ fileExtention;
        Path targetPath= Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            return targetFilePath;
        }catch (IOException e){
            log.error("file was not saved",e);
            return null;
        }


    }

    private String getFileExtension( String originalFilename) {
        if(originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int index = originalFilename.lastIndexOf(".");
        if(index == -1) {
            return "";
        }
        return originalFilename.substring(index+1).toLowerCase()  ;

    }
}
