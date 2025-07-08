package com.hezhaohui.agent.service;

import com.hezhaohui.agent.config.FileStorageConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final FileStorageConfig config;

    public FileStorageService(FileStorageConfig config) {
        this.config = config;
        // 获取上传目录的绝对路径，并将其转换为Path对象
        this.fileStorageLocation = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
        try {
            // 创建上传目录
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            // 如果创建目录失败，抛出RuntimeException异常
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored");
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. 检查文件名中的非法字符
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new RuntimeException("文件名包含非法路径序列: " + fileName);
        }

        // 2. 检查文件扩展名
        String fileExtension = extractFileExtension(fileName);
        boolean isExtensionAllowed = config.getAllowedExtensions().stream()
                .anyMatch(ext -> ext.equalsIgnoreCase(fileExtension)); // equalsIgnoreCase 忽略大小写

        if (!isExtensionAllowed) {
            throw new RuntimeException("文件类型不被允许: " + fileExtension);
        }

        // 3. 检查文件大小
        long maxSizeBytes = DataSize.parse(config.getMaxSize()).toBytes();
        if (file.getSize() > maxSizeBytes) {
            throw new RuntimeException("文件大小超过最大限制(" + config.getMaxSize() + ")");
        }

        // 4. 生成唯一文件名并保存
        String uniqueFileName = generateUniqueFilename(fileName);
        Path targetLocation = fileStorageLocation.resolve(uniqueFileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败: " + fileName, e);
        }
    }

    /* 提取文件扩展名 */
    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            // 提取从 lastDotIndex 开始到字符串末尾的子字符串
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    /* 生成唯一文件名 */
    private String generateUniqueFilename(String fileName) {
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = extractFileExtension(fileName);
        String uniqueSuffix = System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        return baseName + "_" + uniqueSuffix + extension;
    }

}
