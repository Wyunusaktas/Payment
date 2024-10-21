package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.ogu.ceng.Payment.model.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Setting findBySettingKey(String settingKey);
}
