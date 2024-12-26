package tr.edu.ogu.ceng.payment.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.restClientOrder.User;
import tr.edu.ogu.ceng.payment.service.SettingService;

@RestController
@RequestMapping("/api/settings")
public class SettingController {
    

    private final SettingService settingService;

    @Autowired
    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }
    @GetMapping("/user")
    public User getUser() {
        return settingService.getUser();
    }

    // Anahtar ile bir ayarı getirir
    @GetMapping("/key/{settingKey}")
    public ResponseEntity<Setting> getSettingByKey(@PathVariable String settingKey) {
        Optional<Setting> setting = settingService.getSettingByKey(settingKey);
        return setting.isPresent() ?
            new ResponseEntity<>(setting.get(), HttpStatus.OK) :
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Değer ile ayarı getirir
    @GetMapping("/value/{settingValue}")
    public ResponseEntity<Setting> getSettingByValue(@PathVariable String settingValue) {
        Optional<Setting> setting = settingService.getSettingByValue(settingValue);
        return setting.isPresent() ?
            new ResponseEntity<>(setting.get(), HttpStatus.OK) :
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Aktif tüm ayarları getirir
    @GetMapping("/active")
    public ResponseEntity<List<Setting>> getAllActiveSettings() {
        List<Setting> settings = settingService.getAllActiveSettings();
        return settings.isEmpty() ?
            new ResponseEntity<>(HttpStatus.NO_CONTENT) :
            new ResponseEntity<>(settings, HttpStatus.OK);
    }

    // Aktif ayarların sayısını döndürür
    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveSettings() {
        long count = settingService.countActiveSettings();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // Yeni bir ayar ekler
    @PostMapping
    public ResponseEntity<Setting> addSetting(@RequestBody Setting setting) {
        Setting savedSetting = settingService.addSetting(setting);
        return new ResponseEntity<>(savedSetting, HttpStatus.CREATED);
    }

    // Mevcut bir ayarı günceller
    @PutMapping
    public ResponseEntity<Setting> updateSetting(@RequestBody Setting setting) {
        try {
            Setting updatedSetting = settingService.updateSetting(setting);
            return new ResponseEntity<>(updatedSetting, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Bir ayarı siler (silinmiş olarak işaretler)
    @DeleteMapping("/{settingId}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long settingId) {
        try {
            settingService.deleteSetting(settingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Anahtar ile ayarın var olup olmadığını kontrol eder
    @GetMapping("/exists/{settingKey}")
    public ResponseEntity<Boolean> existsSettingByKey(@PathVariable String settingKey) {
        boolean exists = settingService.existsSettingByKey(settingKey);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
