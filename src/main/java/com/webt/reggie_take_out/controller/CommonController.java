package com.webt.reggie_take_out.controller;

import com.webt.reggie_take_out.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
@Api(tags = "文件上传下载接口") // Swagger 类注解
public class CommonController {

  @Value("${reggie.path}")
  private String basePath;

  @PostMapping("/upload")
  @ApiOperation(value = "文件上传", notes = "上传图片文件，返回新生成的文件名")
  public R<String> upload(
          @ApiParam(value = "要上传的文件", required = true) @RequestParam("file") MultipartFile file) {

    log.info("Uploading file: " + file.getOriginalFilename());
    String OriginalFileName = file.getOriginalFilename();
    String suffix =
            "."
                    + Optional.ofNullable(OriginalFileName)
                    .map(FilenameUtils::getExtension)
                    .filter(str -> !str.isEmpty())
                    .orElse("jpg"); // 没后缀就默认 jpg
    String newFileName = UUID.randomUUID().toString() + suffix;

    File dir = new File(basePath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    try {
      file.transferTo(new File(basePath + File.separator + newFileName));
    } catch (IOException e) {
      log.error("文件上传失败", e);
      return R.error("上传失败");
    }
    return R.success(newFileName);
  }

  @GetMapping("/download")
  @ApiOperation(value = "文件下载", notes = "根据文件名下载文件")
  public void download(
          HttpServletResponse response,
          @ApiParam(value = "文件名", required = true) @RequestParam("name") String name) {

    try (FileInputStream fis = new FileInputStream(new File(basePath + name));
         ServletOutputStream outputStream = response.getOutputStream()) {

      response.setContentType("image/jpeg");

      int len;
      byte[] bytes = new byte[1024];
      while ((len = fis.read(bytes)) != -1) {
        outputStream.write(bytes, 0, len);
      }
      outputStream.flush();

    } catch (Exception e) {
      log.error("文件下载失败：{}", name, e);
      throw new RuntimeException(e);
    }
  }
}
