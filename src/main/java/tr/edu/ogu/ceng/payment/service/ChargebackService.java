package tr.edu.ogu.ceng.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tr.edu.ogu.ceng.payment.dto.ChargebackDTO;
import tr.edu.ogu.ceng.payment.entity.Chargeback;
import tr.edu.ogu.ceng.payment.repository.ChargebackRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChargebackService {

    private final ChargebackRepository chargebackRepository;
    private final ModelMapper modelMapper;

    public List<ChargebackDTO> findAll() {
        List<Chargeback> chargebacks = chargebackRepository.findAll();
        return chargebacks.stream()
                .map(chargeback -> modelMapper.map(chargeback, ChargebackDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<ChargebackDTO> findById(Long id) {
        return chargebackRepository.findById(id)
                .map(chargeback -> modelMapper.map(chargeback, ChargebackDTO.class));
    }

    public ChargebackDTO save(ChargebackDTO chargebackDTO) {
        Chargeback chargeback = modelMapper.map(chargebackDTO, Chargeback.class);
        Chargeback savedChargeback = chargebackRepository.save(chargeback);
        return modelMapper.map(savedChargeback, ChargebackDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Chargeback> chargebackOptional = chargebackRepository.findById(id);
        if (chargebackOptional.isPresent()) {
            Chargeback chargeback = chargebackOptional.get();
            chargeback.setDeletedAt(java.time.LocalDateTime.now());
            chargeback.setDeletedBy(deletedBy);
            chargebackRepository.save(chargeback);
        }
    }
}
