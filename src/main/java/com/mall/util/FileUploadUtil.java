package com.mall.util;

import com.mall.exception.BusinessException;
import com.mall.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具 —— 本地存储，预留 OSS 策略切换
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${mall.upload.path}")
    private String uploadPath;

    @Value("${mall.upload.allow-types}")
    private String allowTypes;

    /**
     * 上传文件，返回访问路径
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED.getCode(), "文件为空");
        }

        // 校验文件类型
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        String suffix = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowList = Arrays.asList(allowTypes.split(","));
        if (!allowList.contains(suffix)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        // 生成新文件名：yyyy/MM/dd/uuid.suffix
        String datePath = java.time.LocalDate.now().toString().replace("-", File.separator);
        String newFileName = datePath + File.separator + UUID.randomUUID() + "." + suffix;

        // 写入磁盘
        File dest = new File(uploadPath + newFileName);
        if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED.getCode(), "创建目录失败");
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件写入失败: {}", dest.getAbsolutePath(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return "/upload/" + newFileName.replace(File.separator, "/");
    }
}
