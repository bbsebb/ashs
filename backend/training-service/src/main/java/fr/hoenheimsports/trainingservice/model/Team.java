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

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@UniqueTeam Impossible à mettre en oeuvre, je le met dans Service
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    @Positive
    private int teamNumber;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @Valid
    private List<TrainingSession> trainingSessions = new ArrayList<>();

    public void addTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        trainingSessions.add(trainingSession);
        trainingSession.setTeam(this);
    }

    public void removeTrainingSession(@NonNull TrainingSession trainingSession) {
        Assert.notNull(trainingSession, "TrainingSession must not be null");
        if(trainingSessions.remove(trainingSession)) {
            trainingSession.setTeam(null);
        }
    }


    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<RoleCoach> roleCoaches = new ArrayList<>();

    public void addRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        roleCoaches.add(roleCoach);
        roleCoach.setTeam(this);
    }

    public void removeRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        if(roleCoaches.remove(roleCoach)) {
            roleCoach.setTeam(null);
        }
    }

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

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}