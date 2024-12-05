package tr.edu.ogu.ceng.payment.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.service.SettingService;

public class SettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SettingService settingService;

    @InjectMocks
    private SettingController settingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(settingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetSettingByKey() throws Exception {
        Setting setting = new Setting();
        setting.setSettingKey("testKey");
        setting.setSettingValue("testValue");

        when(settingService.getSettingByKey("testKey")).thenReturn(Optional.of(setting));

        mockMvc.perform(get("/api/settings/key/testKey"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.settingKey").value("testKey"))
            .andExpect(jsonPath("$.settingValue").value("testValue"));
    }

    @Test
    void testGetSettingByKeyNotFound() throws Exception {
        when(settingService.getSettingByKey("nonExistentKey")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/settings/key/nonExistentKey"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetSettingByValue() throws Exception {
        Setting setting = new Setting();
        setting.setSettingKey("testKey");
        setting.setSettingValue("testValue");

        when(settingService.getSettingByValue("testValue")).thenReturn(Optional.of(setting));

        mockMvc.perform(get("/api/settings/value/testValue"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.settingKey").value("testKey"))
            .andExpect(jsonPath("$.settingValue").value("testValue"));
    }

    @Test
    void testGetSettingByValueNotFound() throws Exception {
        when(settingService.getSettingByValue("nonExistentValue")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/settings/value/nonExistentValue"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllActiveSettings() throws Exception {
        Setting setting1 = new Setting();
        setting1.setSettingKey("key1");
        setting1.setSettingValue("value1");

        Setting setting2 = new Setting();
        setting2.setSettingKey("key2");
        setting2.setSettingValue("value2");

        when(settingService.getAllActiveSettings()).thenReturn(List.of(setting1, setting2));

        mockMvc.perform(get("/api/settings/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].settingKey").value("key1"))
            .andExpect(jsonPath("$[1].settingKey").value("key2"));
    }

    @Test
    void testGetAllActiveSettingsNoContent() throws Exception {
        when(settingService.getAllActiveSettings()).thenReturn(List.of());

        mockMvc.perform(get("/api/settings/active"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testCountActiveSettings() throws Exception {
        when(settingService.countActiveSettings()).thenReturn(5L);

        mockMvc.perform(get("/api/settings/active/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(5));
    }

    @Test
    void testAddSetting() throws Exception {
        Setting setting = new Setting();
        setting.setSettingKey("newKey");
        setting.setSettingValue("newValue");

        when(settingService.addSetting(any(Setting.class))).thenReturn(setting);

        mockMvc.perform(post("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setting)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.settingKey").value("newKey"))
            .andExpect(jsonPath("$.settingValue").value("newValue"));
    }

    @Test
    void testUpdateSetting() throws Exception {
        Setting setting = new Setting();
        setting.setSettingKey("updatedKey");
        setting.setSettingValue("updatedValue");

        when(settingService.updateSetting(any(Setting.class))).thenReturn(setting);

        mockMvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setting)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.settingKey").value("updatedKey"))
            .andExpect(jsonPath("$.settingValue").value("updatedValue"));
    }

    @Test
    void testUpdateSettingNotFound() throws Exception {
        Setting setting = new Setting();
        setting.setSettingKey("nonExistentKey");
        setting.setSettingValue("nonExistentValue");

        when(settingService.updateSetting(any(Setting.class))).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setting)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSetting() throws Exception {
        doNothing().when(settingService).deleteSetting(1L);

        mockMvc.perform(delete("/api/settings/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSettingNotFound() throws Exception {
        doThrow(IllegalArgumentException.class).when(settingService).deleteSetting(999L);

        mockMvc.perform(delete("/api/settings/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testExistsSettingByKey() throws Exception {
        when(settingService.existsSettingByKey("existingKey")).thenReturn(true);

        mockMvc.perform(get("/api/settings/exists/existingKey"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testExistsSettingByKeyNotFound() throws Exception {
        when(settingService.existsSettingByKey("nonExistentKey")).thenReturn(false);

        mockMvc.perform(get("/api/settings/exists/nonExistentKey"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(false));
    }
}
