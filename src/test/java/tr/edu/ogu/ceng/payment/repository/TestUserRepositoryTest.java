package tr.edu.ogu.ceng.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}