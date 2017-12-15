package fr.insee.pogues.config;

import io.swagger.jaxrs.config.BeanConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**²
 * Created by acordier on 24/07/17.
 */
public class SwaggerConfig extends HttpServlet {

    private final static Logger logger = LogManager.getLogger(SwaggerConfig.class);

    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            Properties props = getEnvironmentProperties();
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setTitle("Pogues Backoffice");
            beanConfig.setVersion("0.1");
            beanConfig.setDescription("Poges Backoffice API endpoints");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setBasePath(props.getProperty("fr.insee.pogues.api.name"));
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
        props.load(getClass()
                .getClassLoader()
                .getResourceAsStream(propsPath));
        File f = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "pogues-bo.properties"));
        if(f.exists() && !f.isDirectory()) {
            FileReader r = new FileReader(f);
            props.load(r);
            r.close();
        }
        File f2 = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "rmspogfo.properties"));
        if(f2.exists() && !f2.isDirectory()) {
            FileReader r2 = new FileReader(f2);
            props.load(r2);
            r2.close();
        }
        File f3 = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "production.properties"));
        if(f3.exists() && !f3.isDirectory()) {
            FileReader r3 = new FileReader(f3);
            props.load(r3);
            r3.close();
        }
        return props;
    }

}
