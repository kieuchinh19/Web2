package com.web2.chinh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadsDir = Path.of("uploads").toAbsolutePath().normalize();
        Path staticUploadsDir = Path.of("src/main/resources/static/uploads").toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        uploadsDir.toUri().toString(),
                        staticUploadsDir.toUri().toString()
                );
    }
}
