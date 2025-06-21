package fr.hoenheimsports.instagramservice.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sub_attachments")
public class SubAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Embedded
    private MediaEmbeddable media;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "target_id"))
    @AttributeOverride(name = "url", column = @Column(name = "target_url", columnDefinition = "TEXT"))
    private TargetEmbeddable target;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private AttachmentEntity attachment;
}

