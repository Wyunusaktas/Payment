package tr.edu.ogu.ceng.payment.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tr.edu.ogu.ceng.payment.entity.Setting;
import tr.edu.ogu.ceng.payment.repository.SettingRepository;

public class SettingServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    private Setting setting;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito mocks'larını başlat

        // Test için örnek Setting nesnesi oluşturuyoruz
        setting = new Setting();
        setting.setId(1L);
        setting.setSettingKey("currency");
        setting.setSettingValue("USD");
        setting.setDeletedAt(null);
    }

    @Test
    public void testGetSettingByKey() {
        // Mock: SettingRepository.findBySettingKey metodunu mockla
        when(settingRepository.findBySettingKey("currency")).thenReturn(Optional.of(setting));

        // Servis metodu çağrılır
        Optional<Setting> result = settingService.getSettingByKey("currency");

        // Assert: Sonuçların doğru olup olmadığını kontrol et
        assertTrue(result.isPresent());
        assertEquals("currency", result.get().getSettingKey());
    }

    @Test
    public void testGetSettingByKeyNotFound() {
        // Mock: SettingRepository.findBySettingKey metodunu mockla
        when(settingRepository.findBySettingKey("nonexistent")).thenReturn(Optional.empty());

        // Servis metodu çağrılır
        Optional<Setting> result = settingService.getSettingByKey("nonexistent");

        // Assert: Sonucun boş olması gerektiğini kontrol et
        assertFalse(result.isPresent());
    }

    @Test
    public void testExistsSettingByKey() {
        // Mock: SettingRepository.existsBySettingKey metodunu mockla
        when(settingRepository.existsBySettingKey("currency")).thenReturn(true);

        // Servis metodu çağrılır
        boolean exists = settingService.existsSettingByKey("currency");

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertTrue(exists);
    }

    @Test
    public void testExistsSettingByKeyNotFound() {
        // Mock: SettingRepository.existsBySettingKey metodunu mockla
        when(settingRepository.existsBySettingKey("nonexistent")).thenReturn(false);

        // Servis metodu çağrılır
        boolean exists = settingService.existsSettingByKey("nonexistent");

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertFalse(exists);
    }

    @Test
    public void testGetSettingByValue() {
        // Mock: SettingRepository.findBySettingValue metodunu mockla
        when(settingRepository.findBySettingValue("USD")).thenReturn(Optional.of(setting));

        // Servis metodu çağrılır
        Optional<Setting> result = settingService.getSettingByValue("USD");

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertTrue(result.isPresent());
        assertEquals("USD", result.get().getSettingValue());
    }

    @Test
    public void testGetAllActiveSettings() {
        // Mock: SettingRepository.findAllActiveSettings metodunu mockla
        when(settingRepository.findAllActiveSettings()).thenReturn(List.of(setting));

        // Servis metodu çağrılır
        List<Setting> result = settingService.getAllActiveSettings();

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("currency", result.get(0).getSettingKey());
    }

    @Test
    public void testCountActiveSettings() {
        // Mock: SettingRepository.countActiveSettings metodunu mockla
        when(settingRepository.countActiveSettings()).thenReturn(1L);

        // Servis metodu çağrılır
        long count = settingService.countActiveSettings();

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertEquals(1L, count);
    }

    @Test
    public void testAddSetting() {
        // Mock: Yeni bir Setting eklemek için save metodunu mockla
        when(settingRepository.save(setting)).thenReturn(setting);

        // Servis metodu çağrılır
        Setting result = settingService.addSetting(setting);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(setting.getSettingKey(), result.getSettingKey());
    }

    @Test
    public void testUpdateSetting() {
        // Mock: SettingRepository.existsById metodunu mockla
        when(settingRepository.existsById(setting.getId())).thenReturn(true);
        when(settingRepository.save(setting)).thenReturn(setting);

        // Servis metodu çağrılır
        Setting result = settingService.updateSetting(setting);

        // Assert: Sonucun doğru olup olmadığını kontrol et
        assertNotNull(result);
        assertEquals(setting.getId(), result.getId());
    }

    @Test
    public void testUpdateSettingNotFound() {
        // Mock: SettingRepository.existsById metodunu mockla
        when(settingRepository.existsById(setting.getId())).thenReturn(false);

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            settingService.updateSetting(setting);
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("Ayar bulunamadı.", exception.getMessage());
    }

    @Test
    public void testDeleteSetting() {
        // Mock: SettingRepository.findById metodunu mockla
        when(settingRepository.findById(setting.getId())).thenReturn(Optional.of(setting));
        when(settingRepository.save(setting)).thenReturn(setting);

        // Servis metodu çağrılır
        settingService.deleteSetting(setting.getId());

        // Verify: delete işlemi yapıldı mı kontrol et
        assertNotNull(setting.getDeletedAt());
        verify(settingRepository, times(1)).save(setting);
    }

    @Test
    public void testDeleteSettingNotFound() {
        // Mock: SettingRepository.findById metodunu mockla
        when(settingRepository.findById(setting.getId())).thenReturn(Optional.empty());

        // Servis metodu çağrılır ve exception fırlatılır
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            settingService.deleteSetting(setting.getId());
        });

        // Assert: Hata mesajının doğru olup olmadığını kontrol et
        assertEquals("Ayar bulunamadı.", exception.getMessage());
    }
}
