package fr.insee.pogues.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource(value = {
        "classpath:env/${fr.insee.pogues.env:dev}/pogues-bo.properties",
        "file:${catalina.base}/webapps/pogues-bo.properties",
        "file:${catalina.base}/webapps/rmspogfo.properties",
        "file:${catalina.base}/webapps/rmespogfo.properties"
}, ignoreResourceNotFound = true)
public class ApplicationContext {

    @Value("${fr.insee.pogues.persistence.database.host}")
    String dbHost;

    @Value("${fr.insee.pogues.persistence.database.port}")
    String dbPort;

    @Value("${fr.insee.pogues.persistence.database.schema}")
    String dbSchema;

    @Value("${fr.insee.pogues.persistence.database.user}")
    private String dbUser;

    @Value("${fr.insee.pogues.persistence.database.password}")
    private String dbPassword;

    @Value("${fr.insee.pogues.persistence.database.driver}")
    private String dbDriver;

    @Bean
    public HttpClientBuilder httpClientBuilder() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    	SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		return HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLSocketFactory(sslsf);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbSchema));
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        CloseableHttpClient httpClient = httpClientBuilder().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
