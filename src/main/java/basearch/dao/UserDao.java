package basearch.dao;

import java.util.List;
import java.util.Locale;

import basearch.model.User;

public interface UserDao {

	List<User> findAllUsers();

	User getByUsername(String username);
	
	void setLocaleFromLocaleResolver(String username, Locale locale);

}
