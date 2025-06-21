package fr.hoenheimsports.instagramservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// --- Entités JPA (Tables) avec Lombok ---

@Getter
@Setter
@NoArgsConstructor // Constructeur sans arguments requis par JPA
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    private String graphApiId;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private OffsetDateTime createdTime;

    // On exclut les collections des méthodes equals, hashCode et toString pour éviter les problèmes de performance et les récursions infinies.
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttachmentEntity> attachments = new ArrayList<>();

    // Les méthodes utilitaires pour gérer la relation bidirectionnelle sont toujours nécessaires.
    public void addAttachment(AttachmentEntity attachment) {
        attachments.add(attachment);
        attachment.setPost(this);
    }

    public void removeAttachment(AttachmentEntity attachment) {
        attachments.remove(attachment);
        attachment.setPost(null);
    }
}