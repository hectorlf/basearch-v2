package basearch.dao;

import java.util.Collection;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import basearch.model.Language;
import basearch.model.auth.Authority;
import basearch.model.auth.Principal;

public interface AuthDao {

	Principal getByUsername(String username);
	
	/**
	 * Implementation of Spring Security's UserDetailsService
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	
	Authority getAuthority(String username, String authority);

	Principal createPrincipal(String username, String password, boolean enabled, Language language, Collection<String> authorities);

	void deletePrincipal(Principal principal);

	Authority assignAuthority(Principal principal, String authority);

	void unassignAuthority(Principal principal, String authority);

}
