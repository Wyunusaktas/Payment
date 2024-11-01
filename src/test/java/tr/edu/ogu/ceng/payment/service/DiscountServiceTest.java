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
import tr.edu.ogu.ceng.payment.dto.DiscountDTO;
import tr.edu.ogu.ceng.payment.entity.Discount;
import tr.edu.ogu.ceng.payment.repository.DiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class DiscountServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine");

    @MockBean
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ModelMapper modelMapper;

    private Discount discount;
    private DiscountDTO discountDTO;

    @BeforeEach
    void setUp() {
        Mockito.reset(discountRepository);

        discount = new Discount();
        discount.setDiscountId(1L);
        discount.setCode("DISCOUNT10");
        discount.setDiscountAmount(BigDecimal.valueOf(10.00));
        discount.setDiscountType("PERCENTAGE");
        discount.setValidFrom(LocalDateTime.now().minusDays(1));
        discount.setValidTo(LocalDateTime.now().plusDays(1));

        discountDTO = modelMapper.map(discount, DiscountDTO.class);
    }

    @AfterEach
    void tearDown() {
        if (postgreSQLContainer != null && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.close();
        }
    }

    @Test
    void testCreateDiscount() {
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        DiscountDTO createdDiscountDTO = discountService.save(discountDTO);

        assertNotNull(createdDiscountDTO, "Discount creation failed, returned object is null.");
        assertEquals(discountDTO.getDiscountId(), createdDiscountDTO.getDiscountId());
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    void testFindDiscountById_NotFound() {
        when(discountRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<DiscountDTO> foundDiscountDTO = discountService.findById(999L);

        assertFalse(foundDiscountDTO.isPresent(), "Discount should not be found.");
        verify(discountRepository, times(1)).findById(999L);
    }

    @Test
    void testFindDiscountById() {
        when(discountRepository.findById(discount.getDiscountId())).thenReturn(Optional.of(discount));

        Optional<DiscountDTO> foundDiscountDTO = discountService.findById(discount.getDiscountId());

        assertTrue(foundDiscountDTO.isPresent(), "Discount not found.");
        assertEquals(discountDTO.getDiscountId(), foundDiscountDTO.get().getDiscountId());
        verify(discountRepository, times(1)).findById(discount.getDiscountId());
    }

    @Test
    void testUpdateDiscount() {
        discountDTO.setDiscountAmount(BigDecimal.valueOf(15.00));  // BigDecimal olarak ayarlandı
        discount.setDiscountAmount(BigDecimal.valueOf(15.00));     // discount nesnesi de aynı değeri alacak şekilde ayarlandı

        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        DiscountDTO updatedDiscountDTO = discountService.save(discountDTO);

        assertNotNull(updatedDiscountDTO, "Discount update failed, returned object is null.");
        assertEquals(BigDecimal.valueOf(15.00), updatedDiscountDTO.getDiscountAmount(), "Discount amount did not update correctly");
        verify(discountRepository, times(1)).save(any(Discount.class));
    }




    @Test
    void testSoftDeleteDiscount() {
        ArgumentCaptor<Discount> captor = ArgumentCaptor.forClass(Discount.class);

        when(discountRepository.findById(discount.getDiscountId())).thenReturn(Optional.of(discount));
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        discountService.softDelete(discountDTO.getDiscountId(), "testUser");

        verify(discountRepository, times(1)).findById(discount.getDiscountId());
        verify(discountRepository, times(1)).save(captor.capture());

        Discount softDeletedDiscount = captor.getValue();
        assertNotNull(softDeletedDiscount.getDeletedAt(), "DeletedAt should not be null after soft delete.");
        assertEquals("testUser", softDeletedDiscount.getDeletedBy(), "DeletedBy should match the given user.");
    }

    @Test
    void testSoftDeleteDiscount_NotFound() {
        when(discountRepository.findById(anyLong())).thenReturn(Optional.empty());

        discountService.softDelete(999L, "testUser");

        verify(discountRepository, times(1)).findById(999L);
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void testCreateDiscountWithInvalidPercentage() {
        discountDTO.setDiscountAmount(BigDecimal.valueOf(150.0)); // Geçersiz yüzde indirimi

        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        DiscountDTO result = discountService.save(discountDTO);

        assertNotNull(result, "Discount creation failed, returned object is null.");
        assertTrue(result.getDiscountAmount().compareTo(BigDecimal.valueOf(100.0)) <= 0, "Discount percentage should not exceed 100%");
    }


    @Test
    void testFindAllDiscounts() {
        when(discountRepository.findAll()).thenReturn(List.of(discount));

        List<DiscountDTO> discountDTOs = discountService.findAll();

        assertNotNull(discountDTOs, "Discount list is null.");
        assertFalse(discountDTOs.isEmpty(), "Discount list is empty.");
        assertEquals(1, discountDTOs.size(), "Discount list size mismatch.");
        verify(discountRepository, times(1)).findAll();
    }

    @Test
    void testUpdateDiscountWithZeroAmount() {
        // Discount nesnesinin discountAmount alanını sıfır olarak ayarlayın
        discount.setDiscountAmount(BigDecimal.ZERO);
        discountDTO.setDiscountAmount(BigDecimal.ZERO);

        // Repository save işleminde sıfır olarak ayarlanmış discount nesnesini döndür
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        DiscountDTO updatedDiscountDTO = discountService.save(discountDTO);

        assertNotNull(updatedDiscountDTO, "Discount update failed, returned object is null.");
        assertEquals(0, updatedDiscountDTO.getDiscountAmount().compareTo(BigDecimal.ZERO), "Discount amount should be zero");
        verify(discountRepository, times(1)).save(any(Discount.class));
    }


}
