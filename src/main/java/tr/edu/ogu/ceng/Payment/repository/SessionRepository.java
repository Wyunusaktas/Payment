package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
