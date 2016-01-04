package basearch.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.springframework.stereotype.Repository;

import basearch.dao.MetadataDao;
import basearch.model.Language;
import basearch.model.Language_;

@Repository
public class MetadataDaoImpl extends BaseDao implements MetadataDao {

	@Override
	public List<Language> findAllLanguages() {
		List<Language> results = query(Language.class).named(Language.QUERY_ALL).list();
		if (results == null || results.size() == 0) return Collections.emptyList();
		return results;
	}

	@Override
	public Language getDefaultLanguage() {
		Language l = entity(Language.class).by(Language.DEFAULT_LANGUAGE_ID);
		return l;
	}
	
	@Override
	public Language getLanguageBy(String langCode, String region, String variant) throws NonUniqueResultException {
		if (langCode == null || langCode.isEmpty()) throw new IllegalArgumentException("Language code can't be null or empty.");
		if (variant != null && region == null) throw new IllegalArgumentException("Region can't be null if variant is not null.");
		
		PropertyBoundEntityAccessor<Language> accessor = entity(Language.class).with(Language_.langCode, langCode);
		if (region != null) accessor.and(Language_.regionCode, region);
		if (variant != null) accessor.and(Language_.variantCode, variant);
		return accessor.find();
	}

}
