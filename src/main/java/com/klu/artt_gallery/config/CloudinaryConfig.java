package com.klu.artt_gallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "duos018rq",
                "api_key", "928983888554536",
                "api_secret", "qUoTlshyB04o7vc_zCX8z-jDlmA"
        ));
    }
}