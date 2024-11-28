package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.Setting;

@SpringBootTest
public class SettingRepositoryTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SettingRepository settingRepository;

    private Setting setting1;
    private Setting setting2;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        setting1 = new Setting();
        setting1.setSettingKey("payment.gateway.url");
        setting1.setSettingValue("https://payment.example.com");
        settingRepository.save(setting1);

        setting2 = new Setting();
        setting2.setSettingKey("max.retry.attempts");
        setting2.setSettingValue("3");
        settingRepository.save(setting2);
    }

    @Test
    public void testFindById() {
        Optional<Setting> found = settingRepository.findById(setting1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSettingKey()).isEqualTo(setting1.getSettingKey());
    }

    @Test
    public void testFindBySettingKey() {
        Setting found = settingRepository.findBySettingKey("payment.gateway.url");

        assertThat(found).isNotNull();
        assertThat(found.getSettingValue()).isEqualTo("https://payment.example.com");
    }

    @Test
    public void testFindAll() {
        List<Setting> settings = settingRepository.findAll();

        assertThat(settings).hasSize(2);
    }

    @Test
    public void testSaveNewSetting() {
        Setting newSetting = new Setting();
        newSetting.setSettingKey("timeout.seconds");
        newSetting.setSettingValue("30");

        Setting saved = settingRepository.save(newSetting);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSettingKey()).isEqualTo("timeout.seconds");
    }

    @Test
    public void testUpdateExistingSetting() {
        Setting setting = settingRepository.findBySettingKey("payment.gateway.url");
        setting.setSettingValue("https://new-payment.example.com");
        settingRepository.save(setting);

        Setting updated = settingRepository.findBySettingKey("payment.gateway.url");
        assertThat(updated.getSettingValue()).isEqualTo("https://new-payment.example.com");
    }

    @Test
    public void testSoftDelete() {
        Setting setting = settingRepository.findById(setting1.getId()).orElseThrow();
        setting.setDeletedAt(LocalDateTime.now());
        setting.setDeletedBy("testUser");
        settingRepository.save(setting);

        Optional<Setting> deleted = settingRepository.findById(setting1.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    public void testFindBySettingKeyContaining() {
        List<Setting> settings = settingRepository.findBySettingKeyContaining("payment");

        assertThat(settings).hasSize(1);
        assertThat(settings.get(0).getSettingKey()).contains("payment");
    }

    @Test
    public void testFindBySettingKeyStartingWith() {
        List<Setting> settings = settingRepository.findBySettingKeyStartingWith("max");

        assertThat(settings).hasSize(1);
        assertThat(settings.get(0).getSettingKey()).startsWith("max");
    }

    @Test
    public void testFindBySettingValueContaining() {
        List<Setting> settings = settingRepository.findBySettingValueContaining("example");

        assertThat(settings).hasSize(1);
        assertThat(settings.get(0).getSettingValue()).contains("example");
    }

    @Test
    public void testDeleteAndVerifyNotFound() {
        Setting setting = settingRepository.findBySettingKey("payment.gateway.url");
        settingRepository.delete(setting);

        Setting deleted = settingRepository.findBySettingKey("payment.gateway.url");
        assertThat(deleted).isNull();
    }

    @Test
    public void testUniqueSettingKey() {
        Setting duplicateSetting = new Setting();
        duplicateSetting.setSettingKey("payment.gateway.url");
        duplicateSetting.setSettingValue("https://another.example.com");

        assertThrows(Exception.class, () -> {
            settingRepository.save(duplicateSetting);
        });
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
