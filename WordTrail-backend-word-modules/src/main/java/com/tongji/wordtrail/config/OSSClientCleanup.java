package com.tongji.wordtrail.config;

import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aliyun.oss.OSS;

@Component
public class OSSClientCleanup {

    @Autowired
    private OSS ossClient;

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}