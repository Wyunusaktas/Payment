package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.SettingDTO;
import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private final ModelMapper modelMapper;

    public List<SettingDTO> findAll() {
        return settingRepository.findAll()
                .stream()
                .map(setting -> modelMapper.map(setting, SettingDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<SettingDTO> findById(Long id) {
        return settingRepository.findById(id)
                .map(setting -> modelMapper.map(setting, SettingDTO.class));
    }

    public SettingDTO findBySettingKey(String settingKey) {
        Setting setting = settingRepository.findBySettingKey(settingKey);
        return modelMapper.map(setting, SettingDTO.class);
    }

    public SettingDTO save(SettingDTO settingDTO) {
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        Setting savedSetting = settingRepository.save(setting);
        return modelMapper.map(savedSetting, SettingDTO.class);
    }

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
