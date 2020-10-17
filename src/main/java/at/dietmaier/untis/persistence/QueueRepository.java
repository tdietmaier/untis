package at.dietmaier.untis.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueueRepository extends JpaRepository<QueueEntity, Long> {
}
