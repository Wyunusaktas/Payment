package tr.edu.ogu.ceng.payment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.ogu.ceng.payment.dto.RefundDTO;
import tr.edu.ogu.ceng.payment.entity.Refund;
import tr.edu.ogu.ceng.payment.repository.RefundRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final ModelMapper modelMapper;

    public List<RefundDTO> findAll() {
        return refundRepository.findAll()
                .stream()
                .map(refund -> modelMapper.map(refund, RefundDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<RefundDTO> findById(Long id) {
        return refundRepository.findById(id)
                .map(refund -> modelMapper.map(refund, RefundDTO.class));
    }

    public RefundDTO save(RefundDTO refundDTO) {
        Refund refund = modelMapper.map(refundDTO, Refund.class);
        Refund savedRefund = refundRepository.save(refund);
        return modelMapper.map(savedRefund, RefundDTO.class);
    }

    @Transactional
    public void softDelete(Long id, String deletedBy) {
        Optional<Refund> refundOptional = refundRepository.findById(id);
        if (refundOptional.isPresent()) {
            Refund refund = refundOptional.get();
            refund.setDeletedAt(java.time.LocalDateTime.now());
            refund.setDeletedBy(deletedBy);
            refundRepository.save(refund);
        }
    }
}
