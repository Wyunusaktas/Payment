package tr.edu.ogu.ceng.Payment.service;

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

    public void deleteById(Long id) {
        errorLogRepository.deleteById(id);
    }
}
