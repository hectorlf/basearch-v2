package basearch.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import basearch.dao.UserDao;
import basearch.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;

	@Inject
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

}
