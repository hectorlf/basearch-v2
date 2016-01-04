package basearch.model.auth;

import basearch.model.Language;
import basearch.model.PersistentObject_;
import basearch.model.auth.Authority;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-11T22:56:26")
@StaticMetamodel(Principal.class)
public class Principal_ extends PersistentObject_ {

    public static volatile SingularAttribute<Principal, String> password;
    public static volatile SingularAttribute<Principal, Language> language;
    public static volatile SingularAttribute<Principal, Boolean> enabled;
    public static volatile SetAttribute<Principal, Authority> authorities;
    public static volatile SingularAttribute<Principal, String> username;

}