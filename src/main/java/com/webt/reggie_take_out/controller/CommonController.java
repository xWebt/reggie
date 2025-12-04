package com.webt.reggie_take_out.controller;

import com.webt.reggie_take_out.common.R;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
  @Value("${reggie.path}")
  private String basePath;

  @PostMapping("/upload")
  public R<String> upload(MultipartFile file) {
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
      //            e.printStackTrace();
    }
    return R.success(newFileName);
  }

  /**
   * 文件下载
   *
   * @param response
   * @param name
   */
  @GetMapping("/download")
  public void download(HttpServletResponse response, String name) {

    try (FileInputStream fis = new FileInputStream(new File(basePath + name));
        ServletOutputStream outputStream = response.getOutputStream(); ) {

      response.setContentType("image/jpeg");

      int len = 0;
      byte[] bytes = new byte[1024];
      while ((len = fis.read(bytes)) != -1) {
        outputStream.write(bytes, 0, len);
        outputStream.flush();
      }
      ;

    } catch (Exception e) {
      log.error("文件下载失败：{}", name, e);
      throw new RuntimeException(e);
    }
  }
}
