package fr.hoenheimsports.trainingservice.model;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a hall entity used within the training service domain.
 *
 * This class is annotated with JPA and Lombok annotations:
 * - {@code @Entity}: Marks this class as a JPA entity for database persistence.
 * - {@code @Getter} and {@code @Setter}: Lombok annotations to auto-generate getter and setter methods.
 * - {@code @ToString}: Generates a string representation of the object, excluding lazy-loading issues.
 * - {@code @NoArgsConstructor} and {@code @AllArgsConstructor}: Used for generating constructors.
 * - {@code @Builder}: Provides the builder pattern for creating objects.
 *
 * Fields:
 * - `id`: The unique identifier for this hall, generated automatically.
 * - `name`: The name of the hall, which is mandatory and limited to 50 characters.
 * - `address`: An embedded {@link Address} object representing the hall's physical location.
 *
 * Validation:
 * - The `name` field must not be blank and cannot exceed 50 characters to ensure data integrity.
 *
 * Overrides:
 * - The `equals` and `hashCode` methods are overridden to ensure proper equality checks and hashing.
 *   These implementations respect Hibernate proxy behavior for entity comparisons.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50, message = "La nom de la salle ne doit pas dépasser 50 caractères")
    private String name;

    @Embedded
    @NotNull
    @Valid
    private Address address;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @Valid
    private List<TrainingSession> trainingSessions = new ArrayList<>();

    public void addTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        trainingSessions.add(trainingSession);
        trainingSession.setHall(this);
    }

    public void removeTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        if(trainingSessions.remove(trainingSession)) {
            trainingSession.setHall(null);
        }
    }

    /**
     * Compares this Hall object with another object for equality. This method ensures proper equality checks
     * by accounting for potential Hibernate proxy issues and comparing the effective classes and identifiers
     * of the objects.
     *
     * @param o the object to compare with this Hall object
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Hall hall = (Hall) o;
        return getId() != null && Objects.equals(getId(), hall.getId());
    }

    /**
     * Calculates the hash code for this Hall object. This method ensures proper hash code generation
     * by considering potential Hibernate proxy behavior and using the appropriate effective class.
     *
     * @return the hash code of the effective class of this object
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


}