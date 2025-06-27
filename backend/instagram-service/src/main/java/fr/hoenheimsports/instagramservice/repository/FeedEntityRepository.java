package fr.hoenheimsports.instagramservice.repository;

import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedEntityRepository extends JpaRepository<FeedEntity, String> {
}