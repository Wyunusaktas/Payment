package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.Payment.model.Setting;
import tr.edu.ogu.ceng.Payment.repository.SettingRepository;

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

    public void deleteById(Long id) {
        settingRepository.deleteById(id);
    }
}