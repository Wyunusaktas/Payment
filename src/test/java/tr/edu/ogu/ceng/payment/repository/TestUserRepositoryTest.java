package tr.edu.ogu.ceng.payment.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.Setting;

@SpringBootTest
public class TestUserRepositoryTest {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SettingRepository settingRepository;

    static {
        postgreSQLContainer.start();
    }

    @Test
    public void testSaveUser() {
        // Arrange
        Setting setting = new Setting();
        setting.setSettingKey("5");
        setting.setSettingValue("1");
        settingRepository.save(setting);
    }

    @Test
    public void testFindBySettingKey() {
        // Arrange
        Setting setting = new Setting();
        setting.setSettingKey("testKey");
        setting.setSettingValue("testValue");
        settingRepository.save(setting);

        // Act
        Setting foundSetting = settingRepository.findBySettingKey("testKey");

        // Assert
        assertThat(foundSetting).isNotNull();
        assertThat(foundSetting.getSettingKey()).isEqualTo("testKey");
        assertThat(foundSetting.getSettingValue()).isEqualTo("testValue");
    }
    @Query
    @Test
    public void testFindAll() {
        // Arrange
        Setting setting1 = new Setting();
        setting1.setSettingKey("key1");
        setting1.setSettingValue("value1");
        settingRepository.save(setting1);

        Setting setting2 = new Setting();
        setting2.setSettingKey("key2");
        setting2.setSettingValue("value2");
        settingRepository.save(setting2);

        // Act
        List<Setting> settings = settingRepository.findAll();

        // Assert
        assertThat(settings).isNotEmpty();
        assertThat(settings.size()).isGreaterThanOrEqualTo(2);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}