package basearch.model.auth;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import basearch.model.PersistentObject;

@Entity
@Table(name="authorities")
public final class Authority extends PersistentObject implements GrantedAuthority {

	private static final long serialVersionUID = -5992698407860058855L;

	@Basic(optional=false)
	@Column(name="username",nullable=false,insertable=false,updatable=false,length=50)
	private String username;

	@Basic(optional=false)
	@Column(name="authority",nullable=false,length=50)
	private String authority;

	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="username",referencedColumnName="username",nullable=false)
	private Principal principal;

	// constructors

	public Authority() {
		// required by JPA
	}

	public Authority(Principal principal, String authority) {
		if (principal == null) throw new IllegalArgumentException("Parameter principal can't be null");
		if (principal.getUsername() == null || principal.getUsername().isEmpty()) throw new IllegalArgumentException("Parameter principal.getUsername() can't be null or empty");
		if (authority == null || authority.isEmpty()) throw new IllegalArgumentException("Parameter authority can't be null or empty");
		this.authority = authority;
		this.principal = principal;
		this.username = principal.getUsername();
	}

	// getters & setters
	
	@Override
	public String getAuthority() {
		return authority;
	}

	public String getUsername() {
		return username;
	}

	public Principal getPrincipal() {
		return principal;
	}

	// overrides for immutability
	
	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("Authority entities are immutable");
	}

	// equals & hashcode

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Authority)) return false;
		Authority otherAuthority = (Authority)other;
		return this.username.equals(otherAuthority.username) && this.authority.equals(otherAuthority.authority);
	}

	@Override
	public int hashCode() {
		return 17 * this.username.hashCode() * this.authority.hashCode();
	}

}
