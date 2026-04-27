package com.vizsgaremek.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:/var/www/uploads/images/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        String fileLocation = path.startsWith("/") ? "file://" + path : "file:" + path;

        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        fileLocation,
                        "classpath:/static/images/",
                        "classpath:/static/",
                        "classpath:/public/"
                );
    }
}