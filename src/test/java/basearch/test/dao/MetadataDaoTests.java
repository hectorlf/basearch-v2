package basearch.test.dao;

import java.util.Locale;

import javax.persistence.NonUniqueResultException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import basearch.dao.MetadataDao;
import basearch.model.Language;
import basearch.test.BaseDaoTest;

public class MetadataDaoTests extends BaseDaoTest {

	@Autowired
	private MetadataDao metadataDao;

	@Test
	public void testFindAllLanguages() {
		Assert.notEmpty(metadataDao.findAllLanguages());
	}

	@Test
	public void testFindDefaultLanguage() {
		Assert.notNull(metadataDao.getDefaultLanguage());
	}

	@Test
	public void testGetLanguageBy() {
		Assert.notNull(metadataDao.getLanguageBy("es", "ES", null));
		Assert.isNull(metadataDao.getLanguageBy("bla", null, null));
		Assert.isNull(metadataDao.getLanguageBy("bla", "bla", "bla"));
	}
	
	@Test(expected=NonUniqueResultException.class)
	public void testGetLanguageByException() {
		metadataDao.getLanguageBy("en", null, null);
	}

	@Test
	public void testLanguageToLocale() {
		Language l = metadataDao.getLanguageBy("es", "ES", null);
		Assert.notNull(l);
		Assert.isTrue(l.toLocale().equals(new Locale("es", "ES")));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArgumentException1() {
		metadataDao.getLanguageBy(null, null, null);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArgumentException2() {
		metadataDao.getLanguageBy("blablabla", null, "shouldbreak");
	}

}
