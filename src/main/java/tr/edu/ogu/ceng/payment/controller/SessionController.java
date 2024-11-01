package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.SessionDTO;
import tr.edu.ogu.ceng.payment.service.SessionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public List<SessionDTO> getAllSessions() {
        return sessionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<SessionDTO> getSession(@PathVariable Long id) {
        return sessionService.findById(id);
    }

    @PostMapping
    public SessionDTO createSession(@RequestBody SessionDTO sessionDTO) {
        return sessionService.save(sessionDTO);
    }

    @PutMapping("/{id}")
    public SessionDTO updateSession(@PathVariable Long id, @RequestBody SessionDTO sessionDTO) {
        sessionDTO.setSessionId(id);  // ID'yi set et
        return sessionService.save(sessionDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteSession(@PathVariable Long id) {
        sessionService.softDelete(id, "system"); // "system" yerine geçerli kullanıcı bilgisi eklenebilir
    }
}
