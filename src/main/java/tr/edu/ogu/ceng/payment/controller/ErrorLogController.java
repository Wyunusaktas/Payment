package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.ErrorLogDTO;
import tr.edu.ogu.ceng.payment.service.ErrorLogService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/error-log")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @GetMapping
    public List<ErrorLogDTO> getAllErrorLogs() {
        return errorLogService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ErrorLogDTO> getErrorLog(@PathVariable Long id) {
        return errorLogService.findById(id);
    }

    @PostMapping
    public ErrorLogDTO createErrorLog(@RequestBody ErrorLogDTO errorLogDTO) {
        return errorLogService.save(errorLogDTO);
    }

    @PutMapping("/{id}")
    public ErrorLogDTO updateErrorLog(@PathVariable Long id, @RequestBody ErrorLogDTO errorLogDTO) {
        errorLogDTO.setErrorId(id);  // ID'yi ayarla
        return errorLogService.save(errorLogDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteErrorLog(@PathVariable Long id) {
        errorLogService.softDelete(id, "system"); // "system" yerine kullanıcı bilgisi eklenebilir
    }
}
