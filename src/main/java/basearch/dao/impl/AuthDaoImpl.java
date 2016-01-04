package basearch.dao.impl;

import java.util.Collection;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import basearch.dao.AuthDao;
import basearch.model.Language;
import basearch.model.auth.Authority;
import basearch.model.auth.Authority_;
import basearch.model.auth.Principal;
import basearch.model.auth.Principal_;

@Repository
public class AuthDaoImpl extends BaseDao implements AuthDao {

	@Override
	public Principal getByUsername(String username) {
		return entity(Principal.class).with(Principal_.username, username).find();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username == null || username.isEmpty()) throw new UsernameNotFoundException("Username is null or empty");
		Principal p = getByUsername(username);
		if (p == null) throw new UsernameNotFoundException("Username not found in db");
		return p;
	}
	
	@Override
	public Authority getAuthority(String username, String authority) {
		return entity(Authority.class).with(Authority_.username, username).and(Authority_.authority, authority).find();
	}

	@Override
	public Principal createPrincipal(String username, String password, boolean enabled, Language language, Collection<String> authorities) {
		if (username == null || username.isEmpty()) throw new IllegalArgumentException("Parameter username can't be null or empty");
		if (password == null || password.isEmpty()) throw new IllegalArgumentException("Parameter password can't be null or empty");
		if (language == null) throw new IllegalArgumentException("Parameter language can't be null");
		Principal p = new Principal();
		p.setEnabled(enabled);
		p.setLanguage(language);
		p.setPassword(password);
		p.setUsername(username);
		persist(p);
		if (authorities != null && authorities.size() > 0) {
			for (String authority : authorities) {
				Authority a = new Authority(p, authority);
				p.addAuthority(a);
				persist(a);
			}
		}
		return p;
	}

	@Override
	public void deletePrincipal(Principal principal) {
		if (principal == null) throw new IllegalArgumentException("Parameter principal can't be null");
		if (isPersistent(principal)) remove(principal);
		else if (principal.getId() == null || principal.getId().longValue() < 1) throw new IllegalArgumentException("Can't remove entity, it's not persistent and has no valid id");
		else {
			Principal p = entityFor(Principal.class, principal.getId());
			if (p == null) throw new EntityNotFoundException("Tried to remove entity with id " + principal.getId() + " but it doesn't exist");
			else remove(p);
		}
	}

	@Override
	public Authority assignAuthority(Principal principal, String authority) {
		if (authority == null || authority.isEmpty()) throw new IllegalArgumentException("Parameter authority can't be null or empty");
		if (principal == null) throw new IllegalArgumentException("Parameter principal can't be null");
		if (!isPersistent(principal)) principal = refresh(principal);
		Authority a = new Authority(principal, authority);
		principal.addAuthority(a);
		persist(a);
		return a;
	}

	@Override
	public void unassignAuthority(Principal principal, String authority) {
		Authority a = new Authority(principal, authority);
		if (isPersistent(principal)) {
			if (principal.getAuthorities() != null && principal.getAuthorities().contains(a)) {
				principal.getAuthorities().remove(a);
			} else {
				a = getAuthority(principal.getUsername(), authority);
				if (a != null) remove(a);
			}
		} else {
			if (principal.getAuthorities() != null) principal.getAuthorities().remove(a);
			a = getAuthority(principal.getUsername(), authority);
			if (a != null) remove(a);
		}
	}

}
