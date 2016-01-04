package basearch.dao;

import java.util.List;

import javax.persistence.NonUniqueResultException;

import basearch.model.Language;

public interface MetadataDao {

	/*
	 *  Language
	 */

	/**
	 * Returns the list of all configured languages
	 */
	List<Language> findAllLanguages();

	/**
	 * The default language is by definition the first record in the table, and
	 * is expected to have an ID equal to Language.DEFAULT_LANGUAGE_ID
	 */
	Language getDefaultLanguage();
	
	/**
	 * Queries languages by language code, region and variant.
	 * Language code can't be null, region and variant can.
	 * If more than one is found, exception is thrown.
	 */
	Language getLanguageBy(String langCode, String region, String variant) throws NonUniqueResultException;
	
}
