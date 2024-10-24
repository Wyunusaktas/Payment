package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.Payment.model.Setting;
import tr.edu.ogu.ceng.Payment.service.SettingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public List<Setting> getAllSettings() {
        return settingService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Setting> getSetting(@PathVariable Long id) {
        return settingService.findById(id);
    }

    @GetMapping("/key/{key}")
    public Setting getSettingByKey(@PathVariable String key) {
        return settingService.findBySettingKey(key);
    }

    @PostMapping
    public Setting createSetting(@RequestBody Setting setting) {
        return settingService.save(setting);
    }

    @PutMapping("/{id}")
    public Setting updateSetting(@PathVariable Long id, @RequestBody Setting setting) {
        setting.setId(id);  // ID'yi ayarla
        return settingService.save(setting);
    }

    @DeleteMapping("/{id}")
    public void deleteSetting(@PathVariable Long id) {
        settingService.deleteById(id);
    }
}
