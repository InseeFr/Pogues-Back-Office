package fr.insee.pogues.config;

import io.swagger.jaxrs.config.BeanConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by acordier on 24/07/17.
 */
public class SwaggerConfig extends HttpServlet {

    private final static Logger logger = LogManager.getLogger(SwaggerConfig.class);

    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            Properties props = getEnvironmentProperties();
            logger.debug("XXX api host: " + props.getProperty("fr.insee.pogues.api.host"));
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setTitle("Pogues Backoffice");
            beanConfig.setVersion("0.1");
            beanConfig.setDescription("Poges Backoffice API endpoints");
            beanConfig.setSchemes(new String[]{"http"});
            //TODO Externalize the parameter
            beanConfig.setBasePath("/rmspogfo/pogues");
            beanConfig.setHost(props.getProperty("fr.insee.pogues.api.host"));
            beanConfig.setResourcePackage("fr.insee.pogues.webservice.rest");
            beanConfig.setScan(true);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }


    private Properties getEnvironmentProperties() throws IOException {
        Properties props = new Properties();
        String env = System.getProperty("fr.insee.pogues.env");
        if(null == env) {
            env = "dv";
        }
        String propsPath = String.format("env/%s/pogues-bo.properties", env);
        System.out.println("STREAM: " + getClass()
                .getClassLoader()
                .getResourceAsStream(propsPath));
        props.load(getClass()
                .getClassLoader()
                .getResourceAsStream(propsPath));
        return props;
    }

}
