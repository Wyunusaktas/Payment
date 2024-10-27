package tr.edu.ogu.ceng.Payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Session;
import tr.edu.ogu.ceng.Payment.repository.SessionRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Session> sessionOptional = sessionRepository.findById(id);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            session.setDeletedAt(java.time.LocalDateTime.now());
            session.setDeletedBy(deletedBy);
            sessionRepository.save(session);
        }
    }
}
