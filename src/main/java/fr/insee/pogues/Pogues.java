package fr.insee.pogues;

import fr.insee.pogues.configuration.PropertiesLogger;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "fr.insee.pogues")
@EnableTransactionManagement
@ConfigurationPropertiesScan
@Slf4j
public class Pogues extends SpringBootServletInitializer {

	@Value("${application.timezoneId}")
	private String applicationTimeZoneId;

	public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
		return springApplicationBuilder.sources(Pogues.class).listeners(new PropertiesLogger());
	}

	public static void main(String[] args) {
		configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
	}

	@PostConstruct
	public void executeAfterMain() {
		log.info("Timezone is set to '{}'", applicationTimeZoneId);
		TimeZone.setDefault(TimeZone.getTimeZone(applicationTimeZoneId));
	}

	@EventListener
	public void handleApplicationReady(ApplicationReadyEvent event) {
		log.info("=============== Pogues Back-Office has successfully started. ===============");
	}
}
