package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.model.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public List<Setting> findAll() {
        return settingRepository.findAll();
    }

    public Optional<Setting> findById(Long id) {
        return settingRepository.findById(id);
    }

    public Setting findBySettingKey(String settingKey) {
        return settingRepository.findBySettingKey(settingKey);
    }

    public Setting save(Setting setting) {
        return settingRepository.save(setting);
    }

    // Soft delete işlemi için yeni metod
    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Setting> settingOptional = settingRepository.findById(id);
        if (settingOptional.isPresent()) {
            Setting setting = settingOptional.get();
            setting.setDeletedAt(java.time.LocalDateTime.now());
            setting.setDeletedBy(deletedBy);
            settingRepository.save(setting);
        }
    }
}
