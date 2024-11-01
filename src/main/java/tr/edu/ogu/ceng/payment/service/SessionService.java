package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.SessionDTO;
import tr.edu.ogu.ceng.payment.entity.Session;
import tr.edu.ogu.ceng.payment.repository.SessionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ModelMapper modelMapper;

    public List<SessionDTO> findAll() {
        return sessionRepository.findAll()
                .stream()
                .map(session -> modelMapper.map(session, SessionDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<SessionDTO> findById(Long id) {
        return sessionRepository.findById(id)
                .map(session -> modelMapper.map(session, SessionDTO.class));
    }

    public SessionDTO save(SessionDTO sessionDTO) {
        Session session = modelMapper.map(sessionDTO, Session.class);
        Session savedSession = sessionRepository.save(session);
        return modelMapper.map(savedSession, SessionDTO.class);
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
