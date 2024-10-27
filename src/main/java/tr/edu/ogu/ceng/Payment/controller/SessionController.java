package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Session;
import tr.edu.ogu.ceng.Payment.service.SessionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Session> getSession(@PathVariable Long id) {
        return sessionService.findById(id);
    }

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionService.save(session);
    }

    @PutMapping("/{id}")
    public Session updateSession(@PathVariable Long id, @RequestBody Session session) {
        session.setSessionId(id);  // ID'yi set et
        return sessionService.save(session);
    }

    @DeleteMapping("/{id}")
    public void softDeleteSession(@PathVariable Long id) {
        sessionService.softDelete(id, "system"); // "system" yerine kullanıcı bilgisi eklenebilir
    }
}
