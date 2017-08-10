package fr.insee.pogues.swagger;

import io.swagger.jaxrs.config.BeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by acordier on 24/07/17.
 */
@PropertySource("classpath:${fr.insee.pogues.env:prod}/pogues-bo.properties")
public class SwaggerConfig extends HttpServlet {

    @Autowired
    private Environment env;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Pogues Backoffice");
        beanConfig.setVersion("0.1");
        beanConfig.setDescription("Poges Backoffice API endpoints");
        beanConfig.setSchemes(new String[]{"http"});
        //TODO Externalize the parameter
        beanConfig.setBasePath("/rmspogfo/pogues");
        //beanConfig.setHost("dvrmspogfolht01.ad.insee.intra");
        beanConfig.setHost("qfrmspogfolht01.ad.insee.intra");
        beanConfig.setResourcePackage("fr.insee.pogues.webservice.rest");
        beanConfig.setScan(true);
    }
}
