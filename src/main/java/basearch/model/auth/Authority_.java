package basearch.model.auth;

import basearch.model.PersistentObject_;
import basearch.model.auth.Principal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2015-02-11T22:56:26")
@StaticMetamodel(Authority.class)
public final class Authority_ extends PersistentObject_ {

    public static volatile SingularAttribute<Authority, Principal> principal;
    public static volatile SingularAttribute<Authority, String> authority;
    public static volatile SingularAttribute<Authority, String> username;

}