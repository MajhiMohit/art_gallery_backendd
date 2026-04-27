package com.klu.artt_gallery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Value("${CLOUDINARY_CLOUD_NAME:duos018rq}")
    private String cloudName;

    @Value("${CLOUDINARY_API_KEY:928983888554536}")
    private String apiKey;

    @Value("${CLOUDINARY_API_SECRET:qUoTlshyB04o7vc_zCX8z-jDlmA}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret
        ));
    }
}