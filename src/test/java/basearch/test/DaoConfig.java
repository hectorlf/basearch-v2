package basearch.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import basearch.dao.AuthDao;
import basearch.dao.MetadataDao;
import basearch.dao.UserDao;
import basearch.dao.impl.AuthDaoImpl;
import basearch.dao.impl.MetadataDaoImpl;
import basearch.dao.impl.UserDaoImpl;

@Configuration
public class DaoConfig {

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

}
