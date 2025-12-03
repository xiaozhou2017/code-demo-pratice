package com.example.demo.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration;
        // 解析 URI 字符串
        List<String> hosts = parseUris(elasticsearchUrl);
        // 如果有用户名密码，配置认证
        if (!username.isEmpty() && !password.isEmpty()) {
            clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(hosts.toArray(new String[0]))
                    .withBasicAuth(username, password)
                    .withConnectTimeout(5000)
                    .withSocketTimeout(10000)
                    .build();
        } else {
            clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(hosts.toArray(new String[0]))
                    .withConnectTimeout(5000)
                    .withSocketTimeout(10000)
                    .build();
        }

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }

    private List<String> parseUris(String uris) {
        return Arrays.stream(uris.split(","))
                .map(String::trim)
                .map(uri -> {
                    try {
                        URI parsedUri = new URI(uri);
                        return parsedUri.getHost() + ":" + parsedUri.getPort();
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("Invalid Elasticsearch URI: " + uri, e);
                    }
                })
                .collect(Collectors.toList());
    }
}
