package fr.insee.pogues.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Properties;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    Logger logger = LogManager.getLogger(StartupListener.class);

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        logger.debug("Getting application environment");
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)p.get(key);
            logger.debug(key + " = " + value);
        }
    }
}