package tr.edu.ogu.ceng.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tr.edu.ogu.ceng.payment.entity.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

    // Find a setting by its key
    Optional<Setting> findBySettingKey(String settingKey);

    // Check if a setting exists by its key
    boolean existsBySettingKey(String settingKey);

    // Custom query to find setting by value
    @Query("SELECT s FROM Setting s WHERE s.settingValue = ?1 AND s.deletedAt IS NULL")
    Optional<Setting> findBySettingValue(String settingValue);

    // Custom query to get all settings (excluding deleted ones)
    @Query("SELECT s FROM Setting s WHERE s.deletedAt IS NULL")
    List<Setting> findAllActiveSettings();

    // Custom query to count the total number of active settings
    @Query("SELECT COUNT(s) FROM Setting s WHERE s.deletedAt IS NULL")
    long countActiveSettings();
}
