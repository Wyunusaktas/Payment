package tr.edu.ogu.ceng.payment.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.common.TestContainerConfig;
import tr.edu.ogu.ceng.payment.entity.Setting;

@SpringBootTest
public class SettingRepositoryTest  extends  TestContainerConfig {


    @Autowired
    private SettingRepository settingRepository;

    

    @BeforeEach
    public void setUp() {
        // Clean up before each test
        settingRepository.deleteAll();
    }

    @Test
    public void testFindBySettingKey() {
        // Given: create and save a setting
        Setting setting = new Setting();
        setting.setSettingKey("key1");
        setting.setSettingValue("value1");
        settingRepository.save(setting);

        // When: fetch the setting by its key
        Optional<Setting> fetchedSetting = settingRepository.findBySettingKey("key1");

        // Then: check if the setting exists and its value
        assertThat(fetchedSetting).isPresent();
        assertThat(fetchedSetting.get().getSettingValue()).isEqualTo("value1");
    }

    @Test
    public void testExistsBySettingKey() {
        // Given: create and save a setting
        Setting setting = new Setting();
        setting.setSettingKey("key2");
        setting.setSettingValue("value2");
        settingRepository.save(setting);

        // When: check if the setting exists by its key
        boolean exists = settingRepository.existsBySettingKey("key2");

        // Then: validate that the setting exists
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindBySettingValue() {
        // Given: create and save a setting
        Setting setting = new Setting();
        setting.setSettingKey("key3");
        setting.setSettingValue("value3");
        settingRepository.save(setting);

        // When: fetch the setting by its value
        Optional<Setting> fetchedSetting = settingRepository.findBySettingValue("value3");

        // Then: check if the setting is found and its key
        assertThat(fetchedSetting).isPresent();
        assertThat(fetchedSetting.get().getSettingKey()).isEqualTo("key3");
    }

    @Test
    public void testFindAllActiveSettings() {
        // Given: create and save settings
        Setting setting1 = new Setting();
        setting1.setSettingKey("key4");
        setting1.setSettingValue("value4");
        setting1.setDeletedAt(null);
        settingRepository.save(setting1);

        Setting setting2 = new Setting();
        setting2.setSettingKey("key5");
        setting2.setSettingValue("value5");
        setting2.setDeletedAt(null);
        settingRepository.save(setting2);

        // When: fetch all active settings
        List<Setting> activeSettings = settingRepository.findAllActiveSettings();

        // Then: ensure both active settings are returned
        assertThat(activeSettings).hasSize(2);
    }

    @Test
    public void testCountActiveSettings() {
        // Given: create and save settings
        Setting setting1 = new Setting();
        setting1.setSettingKey("key6");
        setting1.setSettingValue("value6");
        setting1.setDeletedAt(null);
        settingRepository.save(setting1);

        Setting setting2 = new Setting();
        setting2.setSettingKey("key7");
        setting2.setSettingValue("value7");
        setting2.setDeletedAt(null);
        settingRepository.save(setting2);

        // When: count the total active settings
        long activeSettingsCount = settingRepository.countActiveSettings();

        // Then: ensure the active settings count is correct
        assertThat(activeSettingsCount).isEqualTo(2);
    }

    @Test
    public void testFindBySettingKeyNotFound() {
        // When: try to find a non-existing setting by its key
        Optional<Setting> fetchedSetting = settingRepository.findBySettingKey("nonExistingKey");

        // Then: check if the result is empty
        assertThat(fetchedSetting).isNotPresent();
    }
}
