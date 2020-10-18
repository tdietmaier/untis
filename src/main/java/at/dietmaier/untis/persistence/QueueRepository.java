package at.dietmaier.untis.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {
}
