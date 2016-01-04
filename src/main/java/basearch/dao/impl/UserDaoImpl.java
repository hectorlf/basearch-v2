package basearch.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import basearch.dao.UserDao;
import basearch.model.User;

@Repository
public class UserDaoImpl extends BaseDao implements UserDao {

	private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
	
	@Override
	public List<User> findAllUsers() {
		List<User> results = allOf(User.class);
		if (results == null || results.size() == 0) return Collections.emptyList();
		return results;
	}

	@Override
	public User getByUsername(String username) {
		if (username == null || username.isEmpty()) throw new IllegalArgumentException("username can't be null");
		// @CacheIndex doesn't seem to work with Criteria queries, so this needs to be a jpql
		TypedQuery<User> q = jpqlQueryFor(User.class, "select u from User u where u.username = :name");
		q.setParameter("name", username);
		try {
			return q.getSingleResult();
		} catch(NoResultException nre) {
			return null;
		}
	}

	@Override
	public void setLocaleFromLocaleResolver(String username, Locale locale) {
		logger.debug("setLocaleFromLocaleResolver() - {} - {}", username, locale);
	}

}
