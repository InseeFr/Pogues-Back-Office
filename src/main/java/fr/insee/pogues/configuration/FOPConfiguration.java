package fr.insee.pogues.configuration;

import fr.insee.pogues.utils.pdf.ClasspathResourceResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.net.URI;

@Configuration
@Slf4j
public class FOPConfiguration {

    private static final URI DEFAULT_BASE_URI = new File(".").toURI();

    private URI imgFolderUri = URI.create(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/img/");

    public FOPConfiguration() {
    }

    private org.apache.fop.configuration.Configuration getFopConfiguration() throws  ConfigurationException {
        org.apache.fop.configuration.Configuration fopConfig = new DefaultConfigurationBuilder().build(getClass().getResourceAsStream("/pdf/fop.xconf"));
        return fopConfig;
    }

    public FopFactory getFopFactory() throws ConfigurationException {
        FopFactoryBuilder builder = new FopFactoryBuilder(imgFolderUri, new ClasspathResourceResolver())
                .setConfiguration(getFopConfiguration());

        FopFactory fopFactory = builder.build();
        fopFactory.getFontManager().setResourceResolver(
                ResourceResolverFactory.createInternalResourceResolver(
                        DEFAULT_BASE_URI,
                        new ClasspathResourceResolver()));

        return fopFactory;
    }
}
