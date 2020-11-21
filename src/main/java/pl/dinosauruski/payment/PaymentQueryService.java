package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    public BigDecimal getOverPayment(Long studentId, Long teacherId) {
        List<Payment> overPayments = paymentRepository.findPaymentWhereOverPaymentByStudentAndTeacher(studentId, teacherId);
        BigDecimal over = overPayments.stream()
                .map(payment -> Optional.ofNullable(payment.getOverPayment()).orElse(BigDecimal.valueOf(0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return over;
    }

}
