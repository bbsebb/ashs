package fr.hoenheimsports.trainingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a coach in the training system.
 *
 * <p>A coach is a person who can train teams and lead training sessions. Each coach has
 * personal information and can have multiple roles associated with different teams.</p>
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
public class Coach {
    /**
     * Unique identifier for the coach.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name of the coach.
     * Cannot be blank.
     */
    @NotBlank
    private String name;

    /**
     * Last name of the coach.
     * Cannot be blank.
     */
    @NotBlank
    private String surname;

    /**
     * Email address of the coach.
     * Must be a valid email format and cannot be null.
     */
    @Email

    private String email;

    /**
     * Phone number of the coach.
     * Must match the pattern of 10-15 digits, optionally starting with a plus sign.
     */
    @Pattern(regexp = "^$|\\+?[0-9]{10,15}", message = "Numéro de téléphone invalide")
    private String phone;

    /**
     * List of roles this coach has with different teams.
     * This is a bidirectional relationship where the coach is the owner.
     */
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<RoleCoach> roleCoaches = new ArrayList<>();

    /**
     * Adds a role to this coach and establishes the bidirectional relationship.
     *
     * @param roleCoach The role to add to this coach, must not be null
     * @throws IllegalArgumentException if roleCoach is null
     */
    public void addRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        roleCoaches.add(roleCoach);
        roleCoach.setCoach(this);
    }

    /**
     * Removes a role from this coach and breaks the bidirectional relationship.
     *
     * @param roleCoach The role to remove from this coach, must not be null
     * @throws IllegalArgumentException if roleCoach is null
     */
    public void removeRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        if (roleCoaches.remove(roleCoach)) {
            roleCoach.setCoach(null);
        }
    }

    /**
     * Compares this coach with another object for equality.
     *
     * <p>Two coaches are considered equal if they have the same non-null ID.</p>
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
        Coach coach = (Coach) o;
        return getId() != null && Objects.equals(getId(), coach.getId());
    }

    /**
     * Returns a hash code value for this coach.
     *
     * <p>The hash code is based on the class of the coach to ensure compatibility with Hibernate proxies.</p>
     *
     * @return a hash code value for this coach
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
