package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    // 配置文件上传的基础路径（应用之外）
    private String baseUploadPath;

    @PostConstruct
    public void init() {
//        // 这里示例使用Windows路径，实际项目请根据环境调整
//        baseUploadPath = "/Users/markchou/uploads/";
//
//        // 确保目录存在
//        Path path = Paths.get(baseUploadPath);
//        if (!Files.exists(path)) {
//            try {
//                Files.createDirectories(path);
//            } catch (IOException e) {
//                throw new RuntimeException("无法创建上传目录: " + baseUploadPath, e);
//            }
//        }
    }

    /**
     * 多文件上传接口
     * POST /api/upload/multiple
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/upload/multiple", consumes = "multipart/form-data")
    @Operation(summary = "多文件上传",
            description = "上传多个文件，支持批量上传")

    public ResponseEntity<UploadResponse> handleMultipleFileUpload(
            @Parameter(description = "上传的文件数组", required = true)
            @RequestPart("files") MultipartFile[] files) {

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest()
                    .body(new UploadResponse("error", "请至少选择一个文件上传", null, null));
        }

        // 限制最大上传文件数量
        int maxFiles = 10;
        if (files.length > maxFiles) {
            return ResponseEntity.badRequest()
                    .body(new UploadResponse("error",
                            "一次最多上传" + maxFiles + "个文件", null, null));
        }

        List<FileInfo> successFiles = new ArrayList<>();
        List<FileError> errorFiles = new ArrayList<>();

        // 处理每个文件
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errorFiles.add(new FileError(file.getOriginalFilename(), "文件为空"));
                continue;
            }

            try {
                // 文件大小校验（例如10MB）
                long maxFileSize = 10 * 1024 * 1024;
                if (file.getSize() > maxFileSize) {
                    errorFiles.add(new FileError(file.getOriginalFilename(),
                            "文件大小超过10MB限制"));
                    continue;
                }

                // 生成唯一文件名，防止冲突
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // 使用时间戳 + UUID 确保唯一性
                String timestamp = String.valueOf(System.currentTimeMillis());
                String uuid = UUID.randomUUID().toString().substring(0, 8);
                String safeFilename = timestamp + "_" + uuid + fileExtension;

                // 构建保存路径（在应用之外）
                Path savePath = Paths.get(baseUploadPath, safeFilename);

                // 保存文件
                Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

                // 记录成功信息
                FileInfo fileInfo = new FileInfo();
                fileInfo.setOriginalName(originalFilename);
                fileInfo.setSavedName(safeFilename);
                fileInfo.setFileSize(file.getSize());
                fileInfo.setContentType(file.getContentType());
                fileInfo.setSavedPath(savePath.toString());
                fileInfo.setAccessUrl("/files/" + safeFilename);

                successFiles.add(fileInfo);

            } catch (IOException e) {
                errorFiles.add(new FileError(file.getOriginalFilename(),
                        "保存失败: " + e.getMessage()));
            }
        }

        // 构建响应
        String status;
        if (successFiles.isEmpty() && !errorFiles.isEmpty()) {
            status = "error";
        } else if (!successFiles.isEmpty() && !errorFiles.isEmpty()) {
            status = "partial_success";
        } else {
            status = "success";
        }

        UploadResponse response = new UploadResponse();
        response.setStatus(status);
        response.setMessage(String.format("上传完成。成功: %d个，失败: %d个",
                successFiles.size(), errorFiles.size()));
        response.setSuccessFiles(successFiles);
        response.setErrorFiles(errorFiles);

        return ResponseEntity.ok(response);
    }

    /**
     * 单文件上传接口（保持兼容）
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<UploadResponse> handleSingleFileUpload(
            @RequestParam("file") MultipartFile file) {
        MultipartFile[] files = new MultipartFile[]{file};
        return handleMultipleFileUpload(files);
    }

    // ========== DTO 定义 - 完整补全 ==========

    /**
     * 上传响应DTO
     */
    public static class UploadResponse {
        private String status; // success, error, partial_success
        private String message;
        private List<FileInfo> successFiles;
        private List<FileError> errorFiles;

        public UploadResponse() {}

        public UploadResponse(String status, String message,
                              List<FileInfo> successFiles, List<FileError> errorFiles) {
            this.status = status;
            this.message = message;
            this.successFiles = successFiles;
            this.errorFiles = errorFiles;
        }

        // getters and setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<FileInfo> getSuccessFiles() {
            return successFiles;
        }

        public void setSuccessFiles(List<FileInfo> successFiles) {
            this.successFiles = successFiles;
        }

        public List<FileError> getErrorFiles() {
            return errorFiles;
        }

        public void setErrorFiles(List<FileError> errorFiles) {
            this.errorFiles = errorFiles;
        }
    }

    /**
     * 文件成功信息DTO
     */
    public static class FileInfo {
        private String originalName; // 原始文件名
        private String savedName;    // 保存后的文件名
        private long fileSize;       // 文件大小（字节）
        private String contentType;  // 文件类型
        private String savedPath;    // 保存路径（完整）
        private String accessUrl;    // 访问URL

        // 无参构造函数
        public FileInfo() {}

        // 全参构造函数（可选）
        public FileInfo(String originalName, String savedName, long fileSize,
                        String contentType, String savedPath, String accessUrl) {
            this.originalName = originalName;
            this.savedName = savedName;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.savedPath = savedPath;
            this.accessUrl = accessUrl;
        }

        // getters and setters
        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getSavedName() {
            return savedName;
        }

        public void setSavedName(String savedName) {
            this.savedName = savedName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getSavedPath() {
            return savedPath;
        }

        public void setSavedPath(String savedPath) {
            this.savedPath = savedPath;
        }

        public String getAccessUrl() {
            return accessUrl;
        }

        public void setAccessUrl(String accessUrl) {
            this.accessUrl = accessUrl;
        }

        /**
         * 获取文件大小的友好显示（KB/MB/GB）
         */
        public String getFormattedFileSize() {
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format("%.2f KB", fileSize / 1024.0);
            } else if (fileSize < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
            } else {
                return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
            }
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "originalName='" + originalName + '\'' +
                    ", savedName='" + savedName + '\'' +
                    ", fileSize=" + fileSize +
                    ", contentType='" + contentType + '\'' +
                    ", savedPath='" + savedPath + '\'' +
                    ", accessUrl='" + accessUrl + '\'' +
                    '}';
        }
    }

    /**
     * 文件错误信息DTO
     */
    public static class FileError {
        private String filename;
        private String error;

        public FileError() {}

        public FileError(String filename, String error) {
            this.filename = filename;
            this.error = error;
        }

        // getters and setters
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "FileError{" +
                    "filename='" + filename + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}
