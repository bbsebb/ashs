package fr.hoenheimsports.facebookservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity representing an access token for the Facebook Graph API.
 * 
 * <p>This class stores information about the Facebook access token used to authenticate
 * requests to the Facebook Graph API. The application stores only one token at a time,
 * which is why the ID is always set to 1.</p>
 * 
 * @since 1.0
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class AccessToken {
    /**
     * Unique identifier for the access token.
     * Always set to 1 as the application only stores one token at a time.
     */
    @Id
    private Long id = 1L;

    /**
     * The actual access token string provided by Facebook.
     */
    private String accessToken;

    /**
     * The type of token (typically "Bearer").
     */
    private String tokenType;

    /**
     * The timestamp when this token will expire.
     */
    private Instant expireIn;

    /**
     * Compares this access token with another object for equality.
     * 
     * <p>Two access tokens are considered equal if they have the same non-null ID.</p>
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AccessToken that = (AccessToken) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /**
     * Returns a hash code value for this access token.
     * 
     * <p>The hash code is based on the class of the access token to ensure compatibility with Hibernate proxies.</p>
     * 
     * @return a hash code value for this access token
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
