package tr.edu.ogu.ceng.payment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@Configuration
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Authenticated kullanıcı adını çekiyoruz
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //return Optional.ofNullable(username).or(() -> Optional.of("system"));  // Varsayılan olarak "system"
        return Optional.of("system");
    }
}
