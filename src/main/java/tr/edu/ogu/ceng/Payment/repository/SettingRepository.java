package tr.edu.ogu.ceng.Payment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.Payment.model.Settings;
import tr.edu.ogu.ceng.Payment.service.SettingService;
@Repository
public interface SettingRepository extends JpaRepository <Settings,Long> {




}
