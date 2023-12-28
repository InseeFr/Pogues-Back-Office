package fr.insee.pogues.config;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

@Component
public class StaticResourcesForFOPConfig {

    final static Logger logger = LogManager.getLogger(StaticResourcesForFOPConfig.class);

    @Value("${fr.insee.pogues.pdf.temp.dir}")
    private Path staticResourcesTempDir;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    private URI imgFolderUri=null;

    @Getter
    @NonNull
    private final Resource fopXconf= resolver.getResource(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/fop.xconf");
        
    public URI getImgFolderUri() {
        if (imgFolderUri==null) {
            imgFolderUri = Objects.requireNonNull(staticResourcesTempDir).resolve("img").toUri();
        }
        return imgFolderUri;
    }

    @PostConstruct
    public void copyAssetResourcesToTempDir() {
        logger.info("Copying static files from "+resolver.getResource(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/")+" to "+staticResourcesTempDir);

        copyFromClasspath(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/img/*", "img");
        copyFromClasspath(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/fonts/*", "fonts");

    }

    private void copyFromClasspath(String locationPattern, String destinationDirectory){
        logger.debug("Process "+locationPattern+ " to "+destinationDirectory);
        Resource[] resources =null;
        try {
            resources = resolver.getResources(locationPattern);
        } catch (IOException e) {
            throw new Error("Error while resolving source pattern : "+locationPattern, e);
        }
        var destination = staticResourcesTempDir.resolve(destinationDirectory);
        for (Resource resource : resources) {
            logger.debug("Process "+resource);
            try {
                FileUtils.copyInputStreamToFile(resource.getInputStream(), destination.resolve(resource.getFilename()).toFile());
            } catch (IOException e) {
                throw new Error("Error while reading resource : "+resource, e);
            }
        }
    }


}
