package com.bit.srb.oss.controller.api;

import com.bit.common.exception.BusinessException;
import com.bit.common.result.R;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.oss.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
//@CrossOrigin
@RestController
@RequestMapping("/api/oss/file")
@Tag(name = "OSS", description = "阿里云文件管理")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(description = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uplode(
            @Parameter(description = "文件", required = true)
            @RequestParam("file")
            MultipartFile file,

            @Parameter(description = "模块", required = true)
            @RequestParam("module") String module) {

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename(); // 文件原本的名字
            String url = fileService.upload(inputStream, module, originalFilename); // 文件上传后的url地址
            return R.ok().message("文件上传成功").data("url",url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @Operation(description = "删除文件")
    @DeleteMapping(value = "/remove")
    public R remove(
            @Parameter(description = "需要删除的文件", required = true)
            @RequestParam("url") String url){
        fileService.removeFile(url);
        return R.ok().message("文件删除成功");
    }
}
