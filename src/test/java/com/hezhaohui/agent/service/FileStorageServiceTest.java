package com.hezhaohui.agent.service;

import com.hezhaohui.agent.config.FileStorageConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileStorageServiceTest {
    @TempDir
    Path tempDir;

    private FileStorageConfig config;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        config = new FileStorageConfig();
        config.setUploadDir(tempDir.toString());
        config.setAllowedExtensions(Arrays.asList(".txt", ".pdf"));
        config.setMaxSize("1MB");

        fileStorageService = new FileStorageService(config);
    }

    @Test
    void storeFile_Success() throws Exception {
        // 创建一个MockMultipartFile对象，模拟一个文件
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "content".getBytes()
        );

        // 调用fileStorageService的storeFile方法，将文件存储到服务器上
        String storedFileName = fileStorageService.storeFile(file);
        // 断言存储的文件名不为空
        assertNotNull(storedFileName);
        // 断言存储的文件名以test_开头
        assertTrue(storedFileName.startsWith("test_"));
        // 断言存储的文件名以.txt结尾
        assertTrue(storedFileName.endsWith(".txt"));

        // 获取存储的文件路径
        Path storedPath = tempDir.resolve(storedFileName);
        // 断言存储的文件存在
        assertTrue(Files.exists(storedPath));
        // 断言存储的文件内容为"content"
        assertEquals("content", Files.readString(storedPath));
    }

    @Test
    void storeFile_WhenInvalidFilename_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "../invalid.txt", "text/plain", "content".getBytes()
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
        assertTrue(exception.getMessage().contains("文件名包含非法路径序列"));
    }

    @Test
    void storeFile_WhenInvalidExtension_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.png", "image/png", "content".getBytes()
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
        assertTrue(exception.getMessage().contains("文件类型不被允许"));
    }

    @Test
    void storeFile_WhenFileTooLarge_ThrowsException() {
        // 创建超过1MB的文件
        byte[] largeContent = new byte[(int)DataSize.parse("1MB").toBytes() + 1];
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", largeContent
        );

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
        assertTrue(exception.getMessage().contains("文件大小超过最大限制"));
    }
}
