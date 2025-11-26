package com.squad10.chatboteasy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "omie")
public class OmieProps {
    private String baseUrl;
}