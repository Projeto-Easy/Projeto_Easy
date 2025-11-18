package com.squad10.chatboteasy.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OmieProps.class)
public class RestClientConfig {

    @Bean
    public RestClient omieRestClient(OmieProps props) {
        HttpClient jdk = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(new JdkClientHttpRequestFactory(jdk))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
