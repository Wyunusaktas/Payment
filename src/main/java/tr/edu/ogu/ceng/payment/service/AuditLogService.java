package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.AuditLogDTO;
import tr.edu.ogu.ceng.payment.entity.AuditLog;
import tr.edu.ogu.ceng.payment.repository.AuditLogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ModelMapper modelMapper;

    public List<AuditLogDTO> findAll() {
        return auditLogRepository.findAll().stream()
                .map(auditLog -> modelMapper.map(auditLog, AuditLogDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<AuditLogDTO> findById(Long id) {
        return auditLogRepository.findById(id)
                .map(auditLog -> modelMapper.map(auditLog, AuditLogDTO.class));
    }

    public AuditLogDTO save(AuditLogDTO auditLogDTO) {
        AuditLog auditLog = modelMapper.map(auditLogDTO, AuditLog.class);
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);
        return modelMapper.map(savedAuditLog, AuditLogDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<AuditLog> auditLogOptional = auditLogRepository.findById(id);
        if (auditLogOptional.isPresent()) {
            AuditLog auditLog = auditLogOptional.get();
            auditLog.setDeletedAt(java.time.LocalDateTime.now());
            auditLog.setDeletedBy(deletedBy);
            auditLogRepository.save(auditLog);
        }
    }
}
