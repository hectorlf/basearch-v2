package basearch.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.CacheIndex;

@Entity
@Table(name="users")
@CacheIndex(columnNames={"username"}, updateable=false)
public class User extends PersistentObject {

	@Basic(optional=false)
	@Column(name="username",length=50,nullable=false,insertable=false,updatable=false,unique=true)
	private String username;
	
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="language_id",nullable=false,insertable=false,updatable=false)
	private Language language;

	// getters & setters
	
	public String getUsername() {
		return username;
	}

	public Language getLanguage() {
		return language;
	}

	// overrides for immutability
	
	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("User entities are immutable");
	}

}
