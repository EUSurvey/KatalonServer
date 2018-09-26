package eu.europa.eusurvey.katalon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class KatalonApplication extends SpringBootServletInitializer {
	
	private static Logger logger = LoggerFactory.getLogger(KatalonApplication.class);
	
	private static Class<KatalonApplication> applicationClass = KatalonApplication.class;

	public static void main(String[] args) {
		SpringApplication.run(KatalonApplication.class, args);
		logger.info("------------------------------------------------------------------");
		logger.info("----------------STARTING KATALON APPLICATION SERVER---------------");
		logger.info("------------------------------------------------------------------");
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

}
