package fr.hoenheimsports.instagramservice.repository;

import fr.hoenheimsports.instagramservice.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, String> {
}