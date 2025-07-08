package fr.hoenheimsports.trainingservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Entity representing the relationship between a coach and a team with a specific role.
 * 
 * <p>This class serves as a join entity to represent the many-to-many relationship
 * between coaches and teams, with additional information about the role of the coach
 * in the team.</p>
 * 
 * @since 1.0
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCoach {
    /**
     * Unique identifier for the role-coach relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The coach associated with this role.
     * This field cannot be null.
     */
    @ManyToOne
    @NotNull
    private Coach coach;

    /**
     * The team associated with this role.
     * This field cannot be null.
     */
    @ManyToOne
    @NotNull
    private Team team;

    /**
     * The specific role the coach has in the team.
     * This field cannot be null and is stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    /**
     * Compares this role-coach relationship with another object for equality.
     * 
     * <p>Two role-coach relationships are considered equal if they have the same non-null ID.</p>
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
        RoleCoach roleCoach = (RoleCoach) o;
        return getId() != null && Objects.equals(getId(), roleCoach.getId());
    }

    /**
     * Returns a hash code value for this role-coach relationship.
     * 
     * <p>The hash code is based on the class of the relationship to ensure compatibility with Hibernate proxies.</p>
     * 
     * @return a hash code value for this role-coach relationship
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
