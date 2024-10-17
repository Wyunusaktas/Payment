package tr.edu.ogu.ceng.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.edu.ogu.ceng.Payment.model.Settings;
import tr.edu.ogu.ceng.Payment.service.SettingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/{id}")

    public Settings getSetting(@PathVariable Long id){
        //settingten ne dönüyo ona bak
        return settingService.getSetting(id);
    }


}
