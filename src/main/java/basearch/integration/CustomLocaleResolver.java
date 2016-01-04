package basearch.integration;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import basearch.dao.MetadataDao;
import basearch.dao.UserDao;
import basearch.model.Language;
import basearch.model.User;

public class CustomLocaleResolver extends CookieGenerator implements LocaleContextResolver {

	private static final Logger logger = LoggerFactory.getLogger(CustomLocaleResolver.class);

	private static final String STORED_LOCALE_KEY = "CustomLocaleResolver.STORED_LOCALE";

	private MetadataDao metadataDao;
	private UserDao userDao;

	@Inject
	public CustomLocaleResolver(MetadataDao metadataDao, UserDao userDao) {
		this.metadataDao = metadataDao;
		this.userDao = userDao;
		setCookieName(Constants.LOCALE_RESOLVER_COOKIE_NAME);
	}

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		logger.debug("resolveLocale()");
		// spring will call this method whenever it feels, so we need to cache the computed locale
		Locale cachedLocale = (Locale)request.getAttribute(STORED_LOCALE_KEY);
		if (cachedLocale != null) {
			logger.debug("Found request-cached locale '{}'", cachedLocale);
			return cachedLocale;
		}
		// not cached! first, resolve authenticated user, if any
		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc != null) {
			Authentication auth = sc.getAuthentication();
			if (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.getPrincipal() != null) {
				User u = userDao.getByUsername(auth.getName());
				Locale l = u.getLanguage().toLocale();
				logger.debug("Found authenticated user with locale '{}'", l);
				request.setAttribute(STORED_LOCALE_KEY, l);
				return l;			
			}
		}
		// we'll need this
		List<Language> supportedLanguages = metadataDao.findAllLanguages();
		// no authenticated user, lets try with cookies
		Cookie cookie = WebUtils.getCookie(request, getCookieName());
		if (cookie != null && cookie.getValue() != null && cookie.getValue().length() != 0) {
			Locale cl = Locale.forLanguageTag(cookie.getValue());
			logger.debug("Parsed cookie value [{}] into locale '{}'", cookie, cl);
			if (isLocaleSupported(supportedLanguages, cl)) {
				logger.debug("Found language cookie with locale '{}'", cl);
				request.setAttribute(STORED_LOCALE_KEY, cl);
				return cl;
			} else {
				logger.debug("Found language cookie but did not match any app language, ignoring cookie");
			}
		}
		// resort to Accept-Language header
		Enumeration<Locale> browserLocales = request.getLocales();
		while (browserLocales.hasMoreElements()) {
			Locale browserLocale = browserLocales.nextElement();
			if (isLocaleSupported(supportedLanguages, browserLocale)) {
				logger.debug("Found app language matching Accept-Language header '{}'", browserLocale);
				request.setAttribute(STORED_LOCALE_KEY, browserLocale);
				return browserLocale;
			} else {
				logger.debug("Passed on Accept-Language header '{}' beacuse no app language matched", browserLocale);
			}
		}
		// no match found, return the default
		logger.debug("No language match, resorting to app default");
		Locale defaultLocale = metadataDao.getDefaultLanguage().toLocale();
		request.setAttribute(STORED_LOCALE_KEY, defaultLocale);
		return defaultLocale;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response,	Locale l) {
		logger.debug("setLocale()");
		throw new UnsupportedOperationException("setLocale() not yet implemented");
	}

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		// currently, timezone is not persisted
		Locale locale = resolveLocale(request);
		return new SimpleLocaleContext(locale);
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext lc) {
		// currently, timezone is not persisted
		setLocale(request, response, lc.getLocale());
	}

	/*
	 * Utility methods
	 */

	private boolean isLocaleSupported(List<Language> supportedLanguages, Locale locale) {
		if (supportedLanguages == null || supportedLanguages.size() == 0) return false;
		for (Language lang : supportedLanguages) {
			if (lang.toLocale().equals(locale)) return true;
		}
		return false;
	}

}
