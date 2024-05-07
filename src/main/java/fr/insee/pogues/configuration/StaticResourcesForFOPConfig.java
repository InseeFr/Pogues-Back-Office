package fr.insee.pogues.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

@Configuration
@Slf4j
public class StaticResourcesForFOPConfig {

    @Value("${application.pdf-temp-dir}")
    private Path staticResourcesTempDir;

    @Value("${feature.oidc.enabled}") boolean oidcEnabled;

    public StaticResourcesForFOPConfig(){
        log.info("test 1");
    }

    public StaticResourcesForFOPConfig(Path staticResourcesTempDir, boolean oidcEnabled){
        log.info("test 2");
    }
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


    public void copyAssetResourcesToTempDir() {
        log.info("Copying static files from "+resolver.getResource(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/")+" to "+staticResourcesTempDir);

        copyFromClasspath(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/img/*", "img");
        copyFromClasspath(ResourceLoader.CLASSPATH_URL_PREFIX+"/pdf/fonts/*", "fonts");

    }

    private void copyFromClasspath(String locationPattern, String destinationDirectory){
        log.debug("Process "+locationPattern+ " to "+destinationDirectory);
        Resource[] resources =null;
        try {
            resources = resolver.getResources(locationPattern);
        } catch (IOException e) {
            throw new Error("Error while resolving source pattern : "+locationPattern, e);
        }
        var destination = staticResourcesTempDir.resolve(destinationDirectory);
        for (Resource resource : resources) {
            log.debug("Process "+resource);
            try {
                FileUtils.copyInputStreamToFile(resource.getInputStream(), destination.resolve(resource.getFilename()).toFile());
            } catch (IOException e) {
                throw new Error("Error while reading resource : "+resource, e);
            }
        }
    }


}
