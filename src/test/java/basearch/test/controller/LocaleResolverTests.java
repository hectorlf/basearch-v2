package basearch.test.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.LocaleResolver;

import basearch.dao.MetadataDao;
import basearch.dao.UserDao;
import basearch.integration.Constants;
import basearch.integration.CustomLocaleResolver;
import basearch.model.Language;
import basearch.test.BaseMvcTest;

@ContextConfiguration(classes=LocaleResolverTests.Config.class)
public class LocaleResolverTests extends BaseMvcTest {

	@Configuration
	public static class Config {
		@Bean
		public MetadataDao metadataDao() {
			Language en_UK = mock(Language.class);
			when(en_UK.getLangCode()).thenReturn("en");
			when(en_UK.getRegionCode()).thenReturn("GB");
			when(en_UK.toLocale()).thenReturn(Locale.forLanguageTag("en-GB"));
			Language es_ES = mock(Language.class);
			when(es_ES.getLangCode()).thenReturn("es");
			when(es_ES.getRegionCode()).thenReturn("ES");
			when(es_ES.toLocale()).thenReturn(Locale.forLanguageTag("es-ES"));
			MetadataDao mock = mock(MetadataDao.class);
			when(mock.getDefaultLanguage()).thenReturn(es_ES);
			when(mock.getLanguageBy("es", "ES", null)).thenReturn(es_ES);
			when(mock.getLanguageBy("en", "GB", null)).thenReturn(en_UK);
			when(mock.findAllLanguages()).thenReturn(Arrays.asList(es_ES, en_UK));
			return mock;
		}

		@Bean
		public UserDao userDao() {
			UserDao mock = mock(UserDao.class);
			return mock;
		}

		@Bean
		public LocaleResolver localeResolver() {
			return new CustomLocaleResolver(metadataDao(), userDao());
		}
	}

	@Test
	public void testSessionLocale() throws Exception {
		// tested in the security tests
	}

	@Test
	public void testNoLocale() throws Exception {
		mockMvc.perform(get("/index.page").locale(null))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testAcceptHeaderLocale1() throws Exception {
		mockMvc.perform(get("/index.page").header("Accept-Language","es-ES"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testAcceptHeaderLocale2() throws Exception {
		mockMvc.perform(get("/index.page").header("Accept-Language","en-GB"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testAcceptHeaderLocale3() throws Exception {
		mockMvc.perform(get("/index.page").header("Accept-Language","en-US, en-GB, en"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testAcceptHeaderLocale4() throws Exception {
		mockMvc.perform(get("/index.page").header("Accept-Language","pt-BR, pt, en"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testAcceptHeaderLocale5() throws Exception {
		// this should kick in as an accept-language locale
		mockMvc.perform(get("/index.page").locale(Locale.forLanguageTag("en-GB")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Welcome")));
	}

	@Test
	public void testCookieLocale1() throws Exception {
		Cookie c = new Cookie(Constants.LOCALE_RESOLVER_COOKIE_NAME, "es-ES");
		mockMvc.perform(get("/index.page").cookie(c))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testCookieLocale2() throws Exception {
		Cookie c = new Cookie(Constants.LOCALE_RESOLVER_COOKIE_NAME, "en-GB");
		mockMvc.perform(get("/index.page").cookie(c))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Welcome")));
	}

	@Test
	public void testCookieLocale3() throws Exception {
		Cookie c = new Cookie(Constants.LOCALE_RESOLVER_COOKIE_NAME, "pt-BR");
		mockMvc.perform(get("/index.page").cookie(c))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

	@Test
	public void testCookieLocale4() throws Exception {
		Cookie c = new Cookie(Constants.LOCALE_RESOLVER_COOKIE_NAME, "arriquitaun");
		mockMvc.perform(get("/index.page").cookie(c))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Bienvenido")));
	}

}
