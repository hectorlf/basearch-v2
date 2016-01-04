package basearch.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import basearch.dao.AuthDao;
import basearch.dao.MetadataDao;
import basearch.dao.UserDao;
import basearch.dao.impl.AuthDaoImpl;
import basearch.dao.impl.MetadataDaoImpl;
import basearch.dao.impl.UserDaoImpl;
import basearch.service.AuthService;
import basearch.service.UserService;
import basearch.service.impl.AuthServiceImpl;
import basearch.service.impl.UserServiceImpl;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:app.properties")
public class AppConfiguration {

	private static String[] messageSourceBasenames = { "applicationResources" };

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasenames(messageSourceBasenames);
		ms.setFallbackToSystemLocale(false);
		return ms;
	}


	// dao layer

	@Bean
	public MetadataDao metadataDao() {
		return new MetadataDaoImpl();
	}

	@Bean
	public AuthDao authDao() {
		return new AuthDaoImpl();
	}

	@Bean
	public UserDao userDao() {
		return new UserDaoImpl();
	}

	// service layer
	
	@Bean
	public AuthService authService() {
		return new AuthServiceImpl(authDao());
	}

	@Bean
	public UserService userService() {
		return new UserServiceImpl(userDao());
	}

}
