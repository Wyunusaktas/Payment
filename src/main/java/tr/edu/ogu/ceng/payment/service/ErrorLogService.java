package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.ErrorLogDTO;
import tr.edu.ogu.ceng.payment.entity.ErrorLog;
import tr.edu.ogu.ceng.payment.repository.ErrorLogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;
    private final ModelMapper modelMapper;

    public List<ErrorLogDTO> findAll() {
        return errorLogRepository.findAll()
                .stream()
                .map(errorLog -> modelMapper.map(errorLog, ErrorLogDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<ErrorLogDTO> findById(Long id) {
        return errorLogRepository.findById(id)
                .map(errorLog -> modelMapper.map(errorLog, ErrorLogDTO.class));
    }

    public ErrorLogDTO save(ErrorLogDTO errorLogDTO) {
        ErrorLog errorLog = modelMapper.map(errorLogDTO, ErrorLog.class);
        ErrorLog savedErrorLog = errorLogRepository.save(errorLog);
        return modelMapper.map(savedErrorLog, ErrorLogDTO.class);
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
