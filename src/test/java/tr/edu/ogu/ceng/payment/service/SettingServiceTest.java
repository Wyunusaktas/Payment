package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.model.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class SettingServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private SettingRepository settingRepository;

    @Autowired
    private SettingService settingService;

    private Setting setting;

    @BeforeEach
    void setUp() {
        reset(settingRepository);

        setting = new Setting();
        setting.setId(1L);
        setting.setSettingKey("siteName");
        setting.setSettingValue("My Application");
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateSetting() {
        when(settingRepository.save(any(Setting.class))).thenReturn(setting);

        Setting createdSetting = settingService.save(setting);

        assertNotNull(createdSetting, "Setting creation failed, returned object is null.");
        assertEquals(setting.getSettingKey(), createdSetting.getSettingKey());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    void testFindSettingById_NotFound() {
        when(settingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Setting> foundSetting = settingService.findById(999L);

        assertFalse(foundSetting.isPresent(), "Setting should not be found.");
        verify(settingRepository, times(1)).findById(999L);
    }

    @Test
    void testFindSettingById() {
        when(settingRepository.findById(setting.getId())).thenReturn(Optional.of(setting));

        Optional<Setting> foundSetting = settingService.findById(setting.getId());

        assertTrue(foundSetting.isPresent(), "Setting not found.");
        assertEquals(setting.getSettingKey(), foundSetting.get().getSettingKey());
        verify(settingRepository, times(1)).findById(setting.getId());
    }

    @Test
    void testUpdateSetting() {
        setting.setSettingValue("Updated Application Name");

        when(settingRepository.save(any(Setting.class))).thenReturn(setting);

        Setting updatedSetting = settingService.save(setting);

        assertNotNull(updatedSetting, "Setting update failed, returned object is null.");
        assertEquals("Updated Application Name", updatedSetting.getSettingValue(), "Setting value did not update correctly.");
        verify(settingRepository, times(1)).save(setting);
    }

    @Test
    void testFindSettingBySettingKey() {
        when(settingRepository.findBySettingKey(setting.getSettingKey())).thenReturn(setting);

        Setting foundSetting = settingService.findBySettingKey(setting.getSettingKey());

        assertNotNull(foundSetting, "Setting not found by key.");
        assertEquals(setting.getSettingKey(), foundSetting.getSettingKey());
        verify(settingRepository, times(1)).findBySettingKey(setting.getSettingKey());
    }

    @Test
    void testSoftDeleteSetting() {
        ArgumentCaptor<Setting> captor = ArgumentCaptor.forClass(Setting.class);

        when(settingRepository.findById(setting.getId())).thenReturn(Optional.of(setting));
        when(settingRepository.save(any(Setting.class))).thenReturn(setting);

        settingService.softDelete(setting.getId(), "testUser");

        verify(settingRepository, times(1)).findById(setting.getId());
        verify(settingRepository, times(1)).save(captor.capture());

        Setting softDeletedSetting = captor.getValue();
        assertNotNull(softDeletedSetting.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedSetting.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteSetting_NotFound() {
        when(settingRepository.findById(anyLong())).thenReturn(Optional.empty());

        settingService.softDelete(999L, "testUser");

        verify(settingRepository, times(1)).findById(999L);
        verify(settingRepository, never()).save(any(Setting.class));
    }

    @Test
    void testFindAllSettings() {
        when(settingRepository.findAll()).thenReturn(List.of(setting));

        List<Setting> settingList = settingService.findAll();

        assertNotNull(settingList, "Setting list is null.");
        assertFalse(settingList.isEmpty(), "Setting list is empty.");
        assertEquals(1, settingList.size(), "Setting list size mismatch.");
        verify(settingRepository, times(1)).findAll();
    }

    @Test
    void testFindBySettingKeyReturnsSetting() {
        Setting setting = new Setting();
        setting.setSettingKey("currency");
        when(settingRepository.findBySettingKey("currency")).thenReturn(setting);

        Setting result = settingService.findBySettingKey("currency");

        assertNotNull(result, "Setting should not be null when found by key");
        assertEquals("currency", result.getSettingKey(), "Setting key should match");
    }

}
