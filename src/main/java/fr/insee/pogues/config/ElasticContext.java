package fr.insee.pogues.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticContext {

    @Value("${fr.insee.pogues.elasticsearch.cluster.name}")
    private String clusterName;

    @Value("${fr.insee.pogues.elasticsearch.host}")
    private String host;

    @Value("${fr.insee.pogues.elasticsearch.port}")
    private int port;

    @Bean
    public RestHighLevelClient client() throws Exception {
        RestClient lowLevelClient = RestClient.builder(
                new HttpHost(host, port, "http")).build();
        return new RestHighLevelClient(lowLevelClient);
    }

}
