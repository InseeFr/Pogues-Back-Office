package fr.insee.pogues.config;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@PropertySource(value = {
        "classpath:env/${fr.insee.pogues.env:dv}/pogues-bo.properties",
        "file:${catalina.home}/webapps/application.properties"
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

    @Value("${fr.insee.ntlm.user}")
    private String ntlmUser;

    @Value("${fr.insee.ntlm.password}")
    private String ntlmPassword;

    @Value("${fr.insee.ntlm.domain}")
    private String ntlmDomain;

    @Bean
    public HttpClientBuilder httpClientBuilder() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE);
        Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                .build();
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(ntlmUser, ntlmPassword, null, ntlmDomain));
        return HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLSocketFactory(sslsf)
                .useSystemProperties()
                .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultCredentialsProvider(credsProvider);
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
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}
