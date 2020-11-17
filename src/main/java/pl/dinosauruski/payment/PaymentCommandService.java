package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class PaymentCommandService {
    private PaymentRepository paymentRepository;
    private TeacherQueryService teacherQueryService;
    private PaymentQueryService paymentQueryService;
    private LessonCommandService lessonCommandService;

    public void create(PaymentDTO paymentDTO, Long teacherId) {
        Payment payment = new Payment();
        payment.setDate(paymentDTO.getDate());
        payment.setStudent(paymentDTO.getStudent());
        payment.setSum(paymentDTO.getSum());
        payment.setTeacher(teacherQueryService.getOneOrThrow(teacherId));
        Payment savedPayment = paymentRepository.save(payment);
        lessonCommandService.addPayment(savedPayment, teacherId);
    }

    public void update(PaymentDTO paymentDTO) {
        Payment payment = paymentQueryService.getOneByIdOrThrow(paymentDTO.getId());
        payment.setDate(paymentDTO.getDate());
        payment.setStudent(paymentDTO.getStudent());
        payment.setSum(paymentDTO.getSum());
        paymentRepository.save(payment);
    }

    public void delete(Long id) {
        Payment payment = paymentQueryService.getOneByIdOrThrow(id);
        paymentRepository.delete(payment);
    }
}
