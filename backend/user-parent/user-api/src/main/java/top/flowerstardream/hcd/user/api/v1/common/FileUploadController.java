package top.flowerstardream.hcd.user.api.v1.common;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import top.flowerstardream.hcd.tools.service.FileStorageService;
import top.flowerstardream.hcd.tools.utils.FileServiceUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: 花海
 * @Date: 2025/11/04/23:05
 * @Description: 用户文件上传接口
 */
@RestController
@RequestMapping("/api/v1/common/user")
public class FileUploadController {
    private final String prefix = "assets/hcd";

    @Resource
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public String uploadFile(MultipartFile file) throws IOException {
        String filename = FileServiceUtil.getFileName(file);
        InputStream inputStream = file.getInputStream();
        return fileStorageService.uploadImgFile(prefix, filename, inputStream);
    }

}
