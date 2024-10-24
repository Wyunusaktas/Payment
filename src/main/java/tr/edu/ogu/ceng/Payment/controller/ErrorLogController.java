package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.ErrorLog;
import tr.edu.ogu.ceng.Payment.service.ErrorLogService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/error-log")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @GetMapping
    public List<ErrorLog> getAllErrorLogs() {
        return errorLogService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ErrorLog> getErrorLog(@PathVariable Long id) {
        return errorLogService.findById(id);
    }

    @PostMapping
    public ErrorLog createErrorLog(@RequestBody ErrorLog errorLog) {
        return errorLogService.save(errorLog);
    }

    @PutMapping("/{id}")
    public ErrorLog updateErrorLog(@PathVariable Long id, @RequestBody ErrorLog errorLog) {
        errorLog.setErrorId(id);  // ID'yi set et
        return errorLogService.save(errorLog);
    }

    @DeleteMapping("/{id}")
    public void deleteErrorLog(@PathVariable Long id) {
        errorLogService.deleteById(id);
    }
}
