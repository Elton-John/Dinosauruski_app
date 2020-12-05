package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PaymentQueryService {

    private PaymentRepository paymentRepository;

    public List<Payment> getLastTenPaymentsByTeacherId(Long id) {
        List<Payment> allPayments = paymentRepository.findAllByTeacherId(id);
        List<Payment> lastTen = allPayments.stream().limit(10).collect(Collectors.toList());
        return lastTen;
    }

    public PaymentDTO getOneDtoByIdOrThrow(Long id) {
        return paymentRepository.findOneDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Payment getOneByIdOrThrow(Long id) {
        return paymentRepository.findOneById(id).orElseThrow(EntityNotFoundException::new);
    }


}
