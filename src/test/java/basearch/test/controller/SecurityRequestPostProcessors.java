/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package basearch.test.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;

/**
 * Demonstrates how to use a {@link RequestPostProcessor} to add
 * request-building methods for establishing a security context for Spring
 * Security. While these are just examples,
 * <a href="https://jira.springsource.org/browse/SEC-2015">official support</a>
 * for Spring Security is planned.
 *
 * @author Rob Winch
 */
final class SecurityRequestPostProcessors {

	/**
	 * Establish a security context for a user with the specified username. All
	 * details are declarative and do not require that the user actually exists.
	 * This means that the authorities or roles need to be specified too.
	 */
	public static UserRequestPostProcessor user(String username, String credentials) {
		return new UserRequestPostProcessor(username, credentials);
	}


	/**
	 * Establish a security context with the given {@link SecurityContext} and
	 * thus be authenticated with {@link SecurityContext#getAuthentication()}.
	 */
	public SecurityContextRequestPostProcessor securityContext(SecurityContext securityContext) {
		return new SecurityContextRequestPostProcessor(securityContext);
	}


	/** Support class for {@link RequestPostProcessor}'s that establish a Spring Security context */
	private static abstract class SecurityContextRequestPostProcessorSupport {

		private SecurityContextRepository repository = new HttpSessionSecurityContextRepository();

		final void save(Authentication authentication, HttpServletRequest request) {
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication(authentication);
			save(securityContext, request);
		}

		final void save(SecurityContext securityContext, HttpServletRequest request) {
			HttpServletResponse response = new MockHttpServletResponse();

			HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
			this.repository.loadContext(requestResponseHolder);

			request = requestResponseHolder.getRequest();
			response = requestResponseHolder.getResponse();

			this.repository.saveContext(securityContext, request, response);
		}
	}

	public final static class SecurityContextRequestPostProcessor
			extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

		private final SecurityContext securityContext;

		private SecurityContextRequestPostProcessor(SecurityContext securityContext) {
			this.securityContext = securityContext;
		}

		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			save(this.securityContext,request);
			return request;
		}
	}

	public final static class UserRequestPostProcessor
			extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

		private final String username;

		private String rolePrefix = "ROLE_";

		private Object credentials;

		private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		private UserRequestPostProcessor(String username, String credentials) {
			Assert.notNull(username, "username cannot be null");
			this.username = username;
			this.credentials = credentials;
		}

		/**
		 * Specify the roles of the user to authenticate as. This method is similar to
		 * {@link #authorities(GrantedAuthority...)}, but just not as flexible.
		 *
		 * @param roles The roles to populate. Note that if the role does not start with
		 * {@link #rolePrefix(String)} it will automatically be prepended. This means by
		 * default {@code roles("ROLE_USER")} and {@code roles("USER")} are equivalent.
		 * @see #authorities(GrantedAuthority...)
		 * @see #rolePrefix(String)
		 */
		public UserRequestPostProcessor roles(String... roles) {
			this.authorities = new ArrayList<GrantedAuthority>(roles.length);
			for(String role : roles) {
				if(this.rolePrefix == null || role.startsWith(this.rolePrefix)) {
					authorities.add(new SimpleGrantedAuthority(role));
				} else {
					authorities.add(new SimpleGrantedAuthority(this.rolePrefix + role));
				}
			}
			return this;
		}

		/**
		 * Populates the user's {@link GrantedAuthority}'s.
		 * @param authorities
		 * @see #roles(String...)
		 */
		public UserRequestPostProcessor authorities(GrantedAuthority... authorities) {
			this.authorities = Arrays.asList(authorities);
			return this;
		}

		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(this.username, this.credentials, this.authorities);
			save(authentication,request);
			return request;
		}
	}

	private SecurityRequestPostProcessors() {}

}
