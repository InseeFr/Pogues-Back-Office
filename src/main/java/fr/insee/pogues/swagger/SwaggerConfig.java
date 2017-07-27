package fr.insee.pogues.swagger;

import io.swagger.jaxrs.config.BeanConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by acordier on 24/07/17.
 */
public class SwaggerConfig extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Pogues Backofficee");
        beanConfig.setVersion("1.0");
        beanConfig.setDescription("The life and times of Pogues REST endpoints");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("dvrmspogfolht01.ad.insee.intra/rmspogfo");
        beanConfig.setBasePath("/pogues");
        beanConfig.setResourcePackage("fr.insee.pogues.webservice.rest");
        beanConfig.setScan(true);
        beanConfig.setDescription("Poges Backoffice API endpoints");
        beanConfig.setContact("...");
        beanConfig.setLicense("MITs");


    }
}
