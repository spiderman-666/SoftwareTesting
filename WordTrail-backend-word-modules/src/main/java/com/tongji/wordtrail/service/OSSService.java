package com.tongji.wordtrail.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
public class OSSService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    // 上传文件
    public String uploadFile(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "")
                + "-" + file.getOriginalFilename();

        // 上传文件到OSS
        ossClient.putObject(bucketName, fileName, file.getInputStream());

        // 设置URL过期时间为1年
        Date expiration = new Date(System.currentTimeMillis() + 365 * 24 * 3600 * 1000L);
        URL url = ossClient.generatePresignedUrl(bucketName, fileName, expiration);

        return url.toString();
    }

    // 下载文件
    public InputStream downloadFile(String fileName) {
        OSSObject ossObject = ossClient.getObject(bucketName, fileName);
        return ossObject.getObjectContent();
    }

    // 删除文件
    public void deleteFile(String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }
}