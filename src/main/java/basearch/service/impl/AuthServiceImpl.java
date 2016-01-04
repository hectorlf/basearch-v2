package basearch.service.impl;

import javax.inject.Inject;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import basearch.dao.AuthDao;
import basearch.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
	
	private AuthDao authDao;

	@Inject
	public AuthServiceImpl(AuthDao authDao) {
		this.authDao = authDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails u = authDao.loadUserByUsername(username);
		return u;
	}

}
