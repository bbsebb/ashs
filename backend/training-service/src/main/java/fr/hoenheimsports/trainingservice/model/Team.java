package fr.hoenheimsports.trainingservice.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a team in the training system.
 * 
 * <p>A team is a group of players with a specific gender, category, and team number.
 * Each team can have multiple training sessions and can be associated with multiple coaches
 * through different roles.</p>
 * 
 * <p>Note: Team uniqueness is enforced at the service level rather than through annotations.</p>
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
//@UniqueTeam Impossible à mettre en oeuvre, je le met dans Service
public class Team {
    /**
     * Unique identifier for the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Gender of the team (F, M, or N).
     * This field cannot be null and is stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    /**
     * Category of the team (age group or skill level).
     * This field cannot be null and is stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    /**
     * Number of the team within its gender and category.
     * Must be a positive integer.
     */
    @Positive
    private int teamNumber;

    /**
     * List of training sessions associated with this team.
     * This is a bidirectional relationship where the team is the owner.
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @Valid
    private List<TrainingSession> trainingSessions = new ArrayList<>();

    /**
     * Adds a training session to this team and establishes the bidirectional relationship.
     * 
     * @param trainingSession The training session to add to this team, must not be null
     * @throws IllegalArgumentException if trainingSession is null
     */
    public void addTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        trainingSessions.add(trainingSession);
        trainingSession.setTeam(this);
    }

    /**
     * Removes a training session from this team and breaks the bidirectional relationship.
     * 
     * @param trainingSession The training session to remove from this team, must not be null
     * @throws IllegalArgumentException if trainingSession is null
     */
    public void removeTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        if(trainingSessions.remove(trainingSession)) {
            trainingSession.setTeam(null);
        }
    }


    /**
     * List of coach roles associated with this team.
     * This is a bidirectional relationship where the team is the owner.
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<RoleCoach> roleCoaches = new ArrayList<>();

    /**
     * Adds a coach role to this team and establishes the bidirectional relationship.
     * 
     * @param roleCoach The coach role to add to this team, must not be null
     * @throws IllegalArgumentException if roleCoach is null
     */
    public void addRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        roleCoaches.add(roleCoach);
        roleCoach.setTeam(this);
    }

    /**
     * Removes a coach role from this team and breaks the bidirectional relationship.
     * 
     * @param roleCoach The coach role to remove from this team, must not be null
     * @throws IllegalArgumentException if roleCoach is null
     */
    public void removeRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        if(roleCoaches.remove(roleCoach)) {
            roleCoach.setTeam(null);
        }
    }

    /**
     * Compares this team with another object for equality.
     * 
     * <p>Two teams are considered equal if they have the same non-null ID.</p>
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
        Team team = (Team) o;
        return getId() != null && Objects.equals(getId(), team.getId());
    }

    /**
     * Returns a hash code value for this team.
     * 
     * <p>The hash code is based on the class of the team to ensure compatibility with Hibernate proxies.</p>
     * 
     * @return a hash code value for this team
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
