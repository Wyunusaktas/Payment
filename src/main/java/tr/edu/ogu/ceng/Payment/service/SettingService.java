package tr.edu.ogu.ceng.Payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import tr.edu.ogu.ceng.Payment.model.Settings;
import tr.edu.ogu.ceng.Payment.repository.SettingRepository;

@RequiredArgsConstructor
@Service
public class SettingService {

    private final SettingRepository settingRepository;

    @GetMapping
    public Settings getSetting(Long id){
        return settingRepository.getReferenceById(id);
    }
}
