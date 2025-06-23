package fr.hoenheimsports.instagramservice.repository;

import fr.hoenheimsports.instagramservice.model.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentEntityRepository extends JpaRepository<AttachmentEntity, Long> {
}