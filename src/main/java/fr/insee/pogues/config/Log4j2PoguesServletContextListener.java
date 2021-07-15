package fr.insee.pogues.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.logging.log4j.web.Log4jWebSupport;


public class Log4j2PoguesServletContextListener implements ServletContextListener {

	private String log4j2ConfigFile;

	private Log4jServletContextListener listener;

	public Log4j2PoguesServletContextListener() {
		this.listener = new Log4jServletContextListener();
		try {
			this.getEnvironmentProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION,
				log4j2ConfigFile);
		listener.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		listener.contextDestroyed(servletContextEvent);
	}

	private void getEnvironmentProperties() throws IOException {
		Properties props = this.getProperties();
		String log4JExternalFile = props.getProperty("fr.insee.rmespogfo.log.configuration");
		this.log4j2ConfigFile = "log4j2.xml";
		File f = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "log4j2.xml"));
		if (f.exists() && !f.isDirectory()) {
			this.log4j2ConfigFile = String.format("%s/webapps/%s", System.getProperty("catalina.base"), "log4j2.xml");
		}
		File f2 = new File(log4JExternalFile);
		if (f2.exists() && !f2.isDirectory()) {
			this.log4j2ConfigFile = String.format(log4JExternalFile);
		}

	}
	
	private Properties getProperties() throws IOException {
        Properties props = new Properties();
        String env = System.getProperty("fr.insee.pogues.env");
        if(null == env) {
            env = "dev";
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
        File f2 = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "rmes-pogbo.properties"));
        if(f2.exists() && !f2.isDirectory()) {
            FileReader r2 = new FileReader(f2);
            props.load(r2);
            r2.close();
        }
        File f3 = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), "rmespogfo.properties"));
        if(f3.exists() && !f3.isDirectory()) {
            FileReader r3 = new FileReader(f3);
            props.load(r3);
            r3.close();
        }
        return props;
    }

}
