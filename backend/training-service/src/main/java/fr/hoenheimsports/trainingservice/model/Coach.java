package fr.hoenheimsports.trainingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @Email
    @NotNull
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Numéro de téléphone invalide")
    @NotNull
    private String phone;


    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<RoleCoach> roleCoaches = new ArrayList<>();

    public void addRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        roleCoaches.add(roleCoach);
        roleCoach.setCoach(this);
    }

    public void removeRoleCoach(@NonNull RoleCoach roleCoach) {
        Assert.notNull(roleCoach, "RoleCoach must not be null");
        if(roleCoaches.remove(roleCoach)) {
            roleCoach.setCoach(null);
        }
    }

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

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}