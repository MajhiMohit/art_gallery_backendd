package com.klu.artt_gallery.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.emptyMap());

        // Use secure_url (HTTPS) to avoid browser mixed-content blocking
        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl != null && !secureUrl.toString().isBlank()) {
            return secureUrl.toString();
        }
        return uploadResult.get("url").toString();
    }
}