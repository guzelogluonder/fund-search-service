package com.oneriver.fundsearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.oneriver.fundsearch.repository")
public class ElasticSearchConfig {
}
