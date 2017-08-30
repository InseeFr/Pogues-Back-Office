package fr.insee.pogues.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ElasticContext {

    @Value("${fr.insee.pogues.elasticsearch.cluster.name}")
    private String clusterName;

    @Value("${fr.insee.pogues.elasticsearch.host}")
    private String host;

    @Value("${fr.insee.pogues.elasticsearch.port}")
    private int port;

    @Bean
    public Client client() throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    }

}
