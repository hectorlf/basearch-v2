package basearch.model.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.CacheIndex;
import org.springframework.security.core.userdetails.UserDetails;

import basearch.model.Language;
import basearch.model.PersistentObject;

@Entity
@Table(name="users")
public class Principal extends PersistentObject implements UserDetails {

	private static final long serialVersionUID = -2219132465446211105L;

	@Basic(optional=false)
	@Column(name="username",length=50,nullable=false,unique=true)
	@CacheIndex
	private String username;

	@Basic(optional=false)
	@Column(name="password",length=50,nullable=false)
	private String password;

	@Basic(optional=false)
	@Column(name="enabled",length=50,nullable=false)
	private boolean enabled;

	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="language_id",nullable=false)
	private Language language;

	@OneToMany(fetch=FetchType.EAGER,mappedBy="principal",cascade=CascadeType.REMOVE,orphanRemoval=true)
	private Set<Authority> authorities;
	
	// getters & setters

	@Override
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public Collection<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	// UserDetails getters

	@Transient
	@Override
	public boolean isAccountNonExpired() {
		return enabled;
	}
	@Transient
	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}
	@Transient
	@Override
	public boolean isCredentialsNonExpired() {
		return enabled;
	}

	// utility methods

	@Transient
	public void addAuthority(Authority authority) {
		if (authority == null) throw new IllegalArgumentException("Parameter authority can't be null");
		if (authorities == null) authorities = new HashSet<Authority>();
		authorities.add(authority);
	}

}
