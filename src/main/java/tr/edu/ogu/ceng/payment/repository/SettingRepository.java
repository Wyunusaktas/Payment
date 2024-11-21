package tr.edu.ogu.ceng.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Setting findBySettingKey(String settingKey);
    @Query("SELECT s FROM Setting s")
    List<Setting> findall();
}
