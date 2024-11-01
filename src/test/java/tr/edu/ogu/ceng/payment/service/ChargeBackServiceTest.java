package tr.edu.ogu.ceng.payment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tr.edu.ogu.ceng.payment.dto.ChargebackDTO;
import tr.edu.ogu.ceng.payment.entity.Chargeback;
import tr.edu.ogu.ceng.payment.repository.ChargebackRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class ChargeBackServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private ChargebackRepository chargebackRepository;

    @Autowired
    private ChargebackService chargebackService;

    @Autowired
    private ModelMapper modelMapper;

    private Chargeback chargeback;
    private ChargebackDTO chargebackDTO;

    @BeforeEach
    void setUp() {
        Mockito.reset(chargebackRepository);

        chargeback = new Chargeback();
        chargeback.setChargebackId(1L);
        chargeback.setUserId(UUID.randomUUID());
        chargeback.setPayment(null);
        chargeback.setChargebackAmount(BigDecimal.valueOf(100.00));
        chargeback.setReason("Duplicate charge");
        chargeback.setFiledAt(LocalDateTime.now());
        chargeback.setStatus("PENDING");

        chargebackDTO = modelMapper.map(chargeback, ChargebackDTO.class);
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateChargeback() {
        when(chargebackRepository.save(any(Chargeback.class))).thenReturn(chargeback);

        ChargebackDTO createdChargebackDTO = chargebackService.save(chargebackDTO);

        assertNotNull(createdChargebackDTO, "Chargeback creation failed, returned object is null.");
        assertEquals(chargebackDTO.getChargebackId(), createdChargebackDTO.getChargebackId());
        verify(chargebackRepository, times(1)).save(any(Chargeback.class));
    }

    @Test
    void testFindChargebackById_NotFound() {
        when(chargebackRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ChargebackDTO> foundChargeback = chargebackService.findById(999L);

        assertFalse(foundChargeback.isPresent(), "Chargeback should not be found.");
        verify(chargebackRepository, times(1)).findById(999L);
    }

    @Test
    void testFindChargebackById() {
        when(chargebackRepository.findById(chargeback.getChargebackId())).thenReturn(Optional.of(chargeback));

        Optional<ChargebackDTO> foundChargebackDTO = chargebackService.findById(chargebackDTO.getChargebackId());

        assertTrue(foundChargebackDTO.isPresent(), "Chargeback not found.");
        assertEquals(chargebackDTO.getChargebackId(), foundChargebackDTO.get().getChargebackId());
        verify(chargebackRepository, times(1)).findById(chargeback.getChargebackId());
    }

    @Test
    void testUpdateChargeback() {
        // Test veri setinde "Updated reason" değerini ayarla
        chargebackDTO.setReason("Updated reason");

        // mock save işlemi sırasında Chargeback nesnesinin geri dönüşünü güncel sebep ile ayarla
        Chargeback updatedChargeback = new Chargeback();
        updatedChargeback.setReason("Updated reason");
        when(chargebackRepository.save(any(Chargeback.class))).thenReturn(updatedChargeback);

        // Servis çağrısı ve dönen değerlerin doğrulanması
        ChargebackDTO updatedChargebackDTO = chargebackService.save(chargebackDTO);

        assertNotNull(updatedChargebackDTO, "Chargeback update failed, returned object is null.");
        assertEquals("Updated reason", updatedChargebackDTO.getReason(), "Reason did not update correctly.");
        verify(chargebackRepository, times(1)).save(any(Chargeback.class));
    }


    @Test
    void testSoftDeleteChargeback() {
        ArgumentCaptor<Chargeback> captor = ArgumentCaptor.forClass(Chargeback.class);

        when(chargebackRepository.findById(chargeback.getChargebackId())).thenReturn(Optional.of(chargeback));
        when(chargebackRepository.save(any(Chargeback.class))).thenReturn(chargeback);

        chargebackService.softDelete(chargebackDTO.getChargebackId(), "testUser");

        verify(chargebackRepository, times(1)).findById(chargeback.getChargebackId());
        verify(chargebackRepository, times(1)).save(captor.capture());

        Chargeback softDeletedChargeback = captor.getValue();
        assertNotNull(softDeletedChargeback.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedChargeback.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteChargeback_NotFound() {
        when(chargebackRepository.findById(anyLong())).thenReturn(Optional.empty());

        chargebackService.softDelete(999L, "testUser");

        verify(chargebackRepository, times(1)).findById(999L);
        verify(chargebackRepository, never()).save(any(Chargeback.class));
    }

    @Test
    void testFindAllChargebacks() {
        when(chargebackRepository.findAll()).thenReturn(List.of(chargeback));

        List<ChargebackDTO> chargebackDTOs = chargebackService.findAll();

        assertNotNull(chargebackDTOs, "Chargeback list is null.");
        assertFalse(chargebackDTOs.isEmpty(), "Chargeback list is empty.");
        assertEquals(1, chargebackDTOs.size(), "Chargeback list size mismatch.");
        verify(chargebackRepository, times(1)).findAll();
    }

    @Test
    void testSaveChargebackNotNull() {
        Chargeback chargeback = new Chargeback();
        chargeback.setStatus("PENDING");
        when(chargebackRepository.save(any(Chargeback.class))).thenReturn(chargeback);

        ChargebackDTO savedChargebackDTO = chargebackService.save(modelMapper.map(chargeback, ChargebackDTO.class));

        assertNotNull(savedChargebackDTO, "Saved chargeback should not be null");
    }
}
