package basearch.test.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;

import basearch.dao.MetadataDao;
import basearch.dao.UserDao;
import basearch.integration.CustomLocaleResolver;
import basearch.model.Language;
import basearch.model.User;
import basearch.test.BaseSpringTest;
import basearch.test.MvcConfig;

@WebAppConfiguration("file:.")
@ContextConfiguration(classes={MvcConfig.class, SecurityTests.Config.class})
public class SecurityTests extends BaseSpringTest {

	@Configuration
	@EnableWebSecurity
	public static class Config extends WebSecurityConfigurerAdapter {

		@Override
		@Autowired
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.inMemoryAuthentication()
					.withUser("test").password("test").roles("ADMIN")
					.and()
					.withUser("regular").password("regular").roles("USER");
		}
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.csrf().disable()
				.authorizeRequests()
					.antMatchers("/secured.page").hasRole("ADMIN")
				.and()
				.antMatcher("/**").anonymous()
				.and()
				.formLogin()
					.loginPage("/login.page")
					.loginProcessingUrl("/login.action").usernameParameter("username").passwordParameter("password")
					.defaultSuccessUrl("/secured.page")
					.failureUrl("/login.page?error=1");
		}
		
		@Bean
		public MetadataDao metadataDao() {
			Language lang = mock(Language.class);
			when(lang.toLocale()).thenReturn(Locale.forLanguageTag("es-ES"));
			MetadataDao mock = mock(MetadataDao.class);
			when(mock.getDefaultLanguage()).thenReturn(lang);
			return mock;
		}

		@Bean
		public UserDao userDao() {
			Language en_GB = mock(Language.class);
			when(en_GB.getLangCode()).thenReturn("en");
			when(en_GB.getRegionCode()).thenReturn("GB");
			when(en_GB.toLocale()).thenReturn(Locale.forLanguageTag("en-GB"));
			User user = mock(User.class);
			when(user.getLanguage()).thenReturn(en_GB);
			UserDao mock = mock(UserDao.class);
			when(mock.getByUsername("test")).thenReturn(user);
			return mock;
		}

		@Bean
		public LocaleResolver localeResolver() {
			return new CustomLocaleResolver(metadataDao(), userDao());
		}

	}

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(springSecurityFilterChain).build();
	}

	/*
	 * Test methods
	 */

	@Test
	public void testSessionLocale() throws Exception {
		mockMvc.perform(get("/index.page").with(SecurityRequestPostProcessors.user("test", "test").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Welcome")));
	}

	@Test
	public void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/secured.page"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login.page"));
	}

	@Test
	public void accessGranted() throws Exception {
		this.mockMvc.perform(get("/secured.page").with(SecurityRequestPostProcessors.user("test", "test").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("ROLE_ADMIN")));
	}

	@Test
	public void accessDenied() throws Exception {
		this.mockMvc.perform(get("/secured.page").with(SecurityRequestPostProcessors.user("user", "pass").roles("DENIED")))
			.andExpect(status().isForbidden());
	}

	@Test
	public void loginIsAvailable() throws Exception {
		this.mockMvc.perform(get("/login.page"))
			.andExpect(status().isOk());
	}

	@Test
	public void loginPostIsAvailable() throws Exception {
		this.mockMvc.perform(post("/login.action"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	public void userAuthenticates() throws Exception {
		final String username = "test";
		final String password = "test";
		mockMvc.perform(post("/login.action").param("username", username).param("password", password))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/secured.page"))
			.andExpect(new ResultMatcher() {
				public void match(MvcResult mvcResult) throws Exception {
					HttpSession session = mvcResult.getRequest().getSession();
					SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
					Assert.assertEquals(securityContext.getAuthentication().getName(), username);
				}
			});
	}

	@Test
	public void userAuthenticateFails() throws Exception {
		mockMvc.perform(post("/login.action").param("username", "notexistent").param("password", "invalid"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login.page?error=1"))
			.andExpect(new ResultMatcher() {
				public void match(MvcResult mvcResult) throws Exception {
					HttpSession session = mvcResult.getRequest().getSession();
					SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
					Assert.assertNull(securityContext);
				}
			});
	}

}
