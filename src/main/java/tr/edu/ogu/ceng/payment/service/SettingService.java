package tr.edu.ogu.ceng.payment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    // Anahtar ile bir ayarı getirir
    public Optional<Setting> getSettingByKey(String settingKey) {
        return settingRepository.findBySettingKey(settingKey);
    }

    // Anahtar ile ayarın var olup olmadığını kontrol eder
    public boolean existsSettingByKey(String settingKey) {
        return settingRepository.existsBySettingKey(settingKey);
    }

    // Değer ile ayarı getirir
    public Optional<Setting> getSettingByValue(String settingValue) {
        return settingRepository.findBySettingValue(settingValue);
    }

    // Aktif tüm ayarları getirir (silinmiş olmayanlar)
    public List<Setting> getAllActiveSettings() {
        return settingRepository.findAllActiveSettings();
    }

    // Aktif ayarların sayısını döndürür
    public long countActiveSettings() {
        return settingRepository.countActiveSettings();
    }

    // Yeni bir ayar ekler
    public Setting addSetting(Setting setting) {
        return settingRepository.save(setting);
    }

    // Mevcut bir ayarı günceller
    public Setting updateSetting(Setting setting) {
        if (settingRepository.existsById(setting.getId())) {
            return settingRepository.save(setting);
        }
        throw new IllegalArgumentException("Ayar bulunamadı.");
    }

    // Bir ayarı siler (silinmiş olarak işaretler)
    public void deleteSetting(Long settingId) {
        Optional<Setting> setting = settingRepository.findById(settingId);
        if (setting.isPresent()) {
            Setting existingSetting = setting.get();
            existingSetting.setDeletedAt(java.time.LocalDateTime.now()); // Silindi olarak işaretle
            settingRepository.save(existingSetting);
        } else {
            throw new IllegalArgumentException("Ayar bulunamadı.");
        }
    }
}
