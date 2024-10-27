package tr.edu.ogu.ceng.Payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.ErrorLog;
import tr.edu.ogu.ceng.Payment.repository.ErrorLogRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    public List<ErrorLog> findAll() {
        return errorLogRepository.findAll();
    }

    public Optional<ErrorLog> findById(Long id) {
        return errorLogRepository.findById(id);
    }

    public ErrorLog save(ErrorLog errorLog) {
        return errorLogRepository.save(errorLog);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<ErrorLog> errorLogOptional = errorLogRepository.findById(id);
        if (errorLogOptional.isPresent()) {
            ErrorLog errorLog = errorLogOptional.get();
            errorLog.setDeletedAt(java.time.LocalDateTime.now());
            errorLog.setDeletedBy(deletedBy);
            errorLogRepository.save(errorLog);
        }
    }
}
