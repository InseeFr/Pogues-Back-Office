package fr.insee.pogues.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URI;

@Configuration
@Slf4j
public class StaticResourcesForFOPConfig {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Getter
    @NonNull
    private URI imgFolderUri = resolver.getResource(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/img/").getURI();

    public StaticResourcesForFOPConfig() throws IOException {
    }

    public org.apache.fop.configuration.Configuration getFopConfiguration() throws IOException, ConfigurationException {
        return new DefaultConfigurationBuilder().build(resolver.getResource(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/fop.xconf").getInputStream());
    }
}
