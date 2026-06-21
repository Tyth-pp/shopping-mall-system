package com.mall.controller.common;

import com.mall.util.FileUploadUtil;
import com.mall.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 */
@Tag(name = "公共-文件上传")
@RestController
@RequestMapping("/api/common/upload")
public class FileUploadController {

    private final FileUploadUtil fileUploadUtil;

    public FileUploadController(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    @Operation(summary = "上传文件")
    @PostMapping("/file")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String path = fileUploadUtil.upload(file);
        return Result.success(path);
    }
}
