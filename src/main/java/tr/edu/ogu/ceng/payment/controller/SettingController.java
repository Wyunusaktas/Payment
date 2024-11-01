package tr.edu.ogu.ceng.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.edu.ogu.ceng.payment.dto.SettingDTO;
import tr.edu.ogu.ceng.payment.service.SettingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public List<SettingDTO> getAllSettings() {
        return settingService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<SettingDTO> getSetting(@PathVariable Long id) {
        return settingService.findById(id);
    }

    @GetMapping("/key/{key}")
    public SettingDTO getSettingByKey(@PathVariable String key) {
        return settingService.findBySettingKey(key);
    }

    @PostMapping
    public SettingDTO createSetting(@RequestBody SettingDTO settingDTO) {
        return settingService.save(settingDTO);
    }

    @PutMapping("/{id}")
    public SettingDTO updateSetting(@PathVariable Long id, @RequestBody SettingDTO settingDTO) {
        settingDTO.setId(id);  // ID'yi ayarla
        return settingService.save(settingDTO);
    }

    @DeleteMapping("/{id}")
    public void softDeleteSetting(@PathVariable Long id) {
        settingService.softDelete(id, "system");
    }
}
