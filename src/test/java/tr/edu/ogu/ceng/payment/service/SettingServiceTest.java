package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.SettingDTO;
import tr.edu.ogu.ceng.payment.entity.Setting;
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

    @MockBean
    private SettingRepository settingRepository;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ModelMapper modelMapper;

    private SettingDTO settingDTO;

    @BeforeEach
    void setUp() {
        reset(settingRepository);

        settingDTO = new SettingDTO();
        settingDTO.setId(1L);
        settingDTO.setSettingKey("siteName");
        settingDTO.setSettingValue("My Application");
    }



    @Test
    void testCreateSetting() {
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        when(settingRepository.save(any(Setting.class))).thenReturn(setting);

        SettingDTO createdSettingDTO = settingService.save(settingDTO);

        assertNotNull(createdSettingDTO, "Setting creation failed, returned object is null.");
        assertEquals(settingDTO.getSettingKey(), createdSettingDTO.getSettingKey());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    void testFindSettingById_NotFound() {
        when(settingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<SettingDTO> foundSettingDTO = settingService.findById(999L);

        assertFalse(foundSettingDTO.isPresent(), "Setting should not be found.");
        verify(settingRepository, times(1)).findById(999L);
    }

    @Test
    void testFindSettingById() {
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        when(settingRepository.findById(settingDTO.getId())).thenReturn(Optional.of(setting));

        Optional<SettingDTO> foundSettingDTO = settingService.findById(settingDTO.getId());

        assertTrue(foundSettingDTO.isPresent(), "Setting not found.");
        assertEquals(settingDTO.getSettingKey(), foundSettingDTO.get().getSettingKey());
        verify(settingRepository, times(1)).findById(settingDTO.getId());
    }

    @Test
    void testUpdateSetting() {
        settingDTO.setSettingValue("Updated Application Name");
        Setting updatedSetting = modelMapper.map(settingDTO, Setting.class);

        when(settingRepository.save(any(Setting.class))).thenReturn(updatedSetting);

        SettingDTO updatedSettingDTO = settingService.save(settingDTO);

        assertNotNull(updatedSettingDTO, "Setting update failed, returned object is null.");
        assertEquals("Updated Application Name", updatedSettingDTO.getSettingValue(), "Setting value did not update correctly.");
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    void testFindSettingBySettingKey() {
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        when(settingRepository.findBySettingKey(settingDTO.getSettingKey())).thenReturn(setting);

        SettingDTO foundSettingDTO = settingService.findBySettingKey(settingDTO.getSettingKey());

        assertNotNull(foundSettingDTO, "Setting not found by key.");
        assertEquals(settingDTO.getSettingKey(), foundSettingDTO.getSettingKey());
        verify(settingRepository, times(1)).findBySettingKey(settingDTO.getSettingKey());
    }

    @Test
    void testSoftDeleteSetting() {
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        ArgumentCaptor<Setting> captor = ArgumentCaptor.forClass(Setting.class);

        when(settingRepository.findById(settingDTO.getId())).thenReturn(Optional.of(setting));
        when(settingRepository.save(any(Setting.class))).thenReturn(setting);

        settingService.softDelete(settingDTO.getId(), "testUser");

        verify(settingRepository, times(1)).findById(settingDTO.getId());
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
        Setting setting = modelMapper.map(settingDTO, Setting.class);
        when(settingRepository.findAll()).thenReturn(List.of(setting));

        List<SettingDTO> settingDTOList = settingService.findAll();

        assertNotNull(settingDTOList, "Setting list is null.");
        assertFalse(settingDTOList.isEmpty(), "Setting list is empty.");
        assertEquals(1, settingDTOList.size(), "Setting list size mismatch.");
        verify(settingRepository, times(1)).findAll();
    }

    @Test
    void testFindBySettingKeyReturnsSetting() {
        Setting setting = new Setting();
        setting.setSettingKey("currency");
        when(settingRepository.findBySettingKey("currency")).thenReturn(setting);

        SettingDTO result = settingService.findBySettingKey("currency");

        assertNotNull(result, "Setting should not be null when found by key");
        assertEquals("currency", result.getSettingKey(), "Setting key should match");
    }


}
