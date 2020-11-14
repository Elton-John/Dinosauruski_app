package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class PaymentQueryService {
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPaymentByTeacherId(Long id) {
        return paymentRepository.findAllByTeacherId(id);
    }


    public PaymentDTO getOneDtoByIdOrThrow(Long id) {
        return paymentRepository.findOneDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Payment getOneByIdOrThrow(Long id) {
        return paymentRepository.findOneById(id).orElseThrow(EntityNotFoundException::new);
    }
}
