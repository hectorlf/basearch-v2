package basearch.test.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import basearch.dao.AuthDao;
import basearch.dao.MetadataDao;
import basearch.model.Language;
import basearch.model.auth.Authority;
import basearch.model.auth.Principal;
import basearch.test.BaseDaoTest;

public class AuthenticationDaoTests extends BaseDaoTest {

	@Autowired
	private MetadataDao metadataDao;

	@Autowired
	private AuthDao authDao;

	@Test
	public void testPrincipalCreation() {
		Language l = metadataDao.getDefaultLanguage();
		Assert.notNull(l);
		List<String> authorities = new ArrayList<String>(1);
		authorities.add("basicuser");
		Principal p = authDao.createPrincipal("newlycreateduser", "none", true, l, authorities);
		Assert.notNull(p);
		Assert.notNull(authDao.getByUsername("newlycreateduser"));
		Assert.notEmpty(authDao.getByUsername("newlycreateduser").getAuthorities());
	}

	@Test
	public void testPrincipalDeletion() {
		Language l = metadataDao.getDefaultLanguage();
		Assert.notNull(l);
		List<String> authorities = new ArrayList<String>(1);
		authorities.add("basicuser");
		Principal p = authDao.createPrincipal("newlycreateduser", "none", true, l, authorities);
		Assert.notNull(p);
		authDao.deletePrincipal(p);
		Assert.isNull(authDao.getByUsername("newlycreateduser"));
	}

	@Test
	public void testAuthorityCreation() {
		Principal p = (Principal)authDao.getByUsername("test");
		Assert.notNull(p);
		Authority a = authDao.assignAuthority(p, "specialuser");
		Assert.notNull(a);
		Principal ptest = (Principal)authDao.getByUsername(p.getUsername());
		Assert.notEmpty(ptest.getAuthorities());
		boolean found = false;
		for (Authority atest : ptest.getAuthorities()) {
			if (atest.getAuthority().equals("specialuser")) {
				found = true;
				break;
			}
		}
		Assert.isTrue(found);
	}

	@Test
	public void testAuthorityDeletion1() {
		Principal p = (Principal)authDao.getByUsername("test");
		Assert.notNull(p);
		Authority a = authDao.assignAuthority(p, "willbedeleted");
		Assert.notNull(a);
		Principal ptest = (Principal)authDao.getByUsername(p.getUsername());
		Assert.notEmpty(ptest.getAuthorities());
		Iterator<Authority> it = ptest.getAuthorities().iterator();
		while (it.hasNext()) {
			Authority temp = it.next();
			if (temp.getAuthority().equals("willbedeleted")) {
				it.remove();
				break;
			}
		}
		ptest = (Principal)authDao.getByUsername(p.getUsername());
		boolean found = false;
		for (Authority atest : ptest.getAuthorities()) {
			if (atest.getAuthority().equals("willbedeleted")) {
				found = true;
				break;
			}
		}
		Assert.isTrue(!found);
	}
	
	@Test
	public void testAuthorityDeletion2() {
		Principal p = (Principal)authDao.getByUsername("test");
		Assert.notNull(p);
		Authority a = authDao.assignAuthority(p, "willbedeleted");
		Assert.notNull(a);
		Principal ptest = (Principal)authDao.getByUsername(p.getUsername());
		Assert.notEmpty(ptest.getAuthorities());
		authDao.unassignAuthority(p, "willbedeleted");
		ptest = (Principal)authDao.getByUsername(p.getUsername());
		boolean found = false;
		for (Authority atest : ptest.getAuthorities()) {
			if (atest.getAuthority().equals("willbedeleted")) {
				found = true;
				break;
			}
		}
		Assert.isTrue(!found);
	}

	@Test
	public void testUserDetailsService1() {
		Assert.notNull(authDao.loadUserByUsername("test"));
	}

	@Test(expected=UsernameNotFoundException.class)
	public void testUserDetailsService2() {
		authDao.loadUserByUsername("nonexistentuser");
	}

}
