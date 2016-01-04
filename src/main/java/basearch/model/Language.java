package basearch.model;

import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.ReadOnly;
import org.eclipse.persistence.config.QueryHints;

@Entity
@Table(name="languages")
@ReadOnly
@CacheIndex(columnNames={"language","region","variant"}, updateable=false)
@NamedQuery(
	name=Language.QUERY_ALL,
	query="select l from Language l",
	hints={
		@QueryHint(name=QueryHints.QUERY_RESULTS_CACHE,value="true"),
		@QueryHint(name=QueryHints.QUERY_RESULTS_CACHE_SIZE,value="1")
	}
)
public class Language extends PersistentObject {

	public static final Long DEFAULT_LANGUAGE_ID = Long.valueOf(1);
	public static final String QUERY_ALL = "Language.allLanguages";

	@Basic(optional=false)
	@Column(name="language",length=3)
	private String langCode;
	
	@Basic(optional=true)
	@Column(name="region",length=3)
	private String regionCode;

	@Basic(optional=true)
	@Column(name="variant",length=8)
	private String variantCode;

	// getters sinteticos
	
	public Locale toLocale() {
		if (langCode == null || langCode.isEmpty()) return null;
		if (regionCode == null || regionCode.isEmpty()) return new Locale(langCode);
		if (variantCode == null || variantCode.isEmpty()) return new Locale(langCode, regionCode);
		return new Locale(langCode, regionCode, variantCode);
	}
	
	// getters & setters
	
	public String getLangCode() {
		return langCode;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public String getVariantCode() {
		return variantCode;
	}

	// overrides for immutability
	
	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("Language entities are immutable");
	}

}
