package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.payment.dto.PaymentDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class PaymentCommandService {
    private PaymentRepository paymentRepository;
    private PaymentQueryService paymentQueryService;
    private TeacherQueryService teacherQueryService;
    private LessonQueryService lessonQueryService;
    private StudentQueryService studentQueryService;
    private LessonCommandService lessonCommandService;


    public void create(PaymentDTO paymentDTO, Long teacherId) {
        Payment payment = new Payment();
        payment.setDate(paymentDTO.getDate());
        payment.setStudent(paymentDTO.getStudent());
        payment.setSum(paymentDTO.getSum());
        payment.setTeacher(teacherQueryService.getOneOrThrow(teacherId));
        Payment savedPayment = paymentRepository.save(payment);
        addPayment(savedPayment, teacherId);
    }

    public void addPayment(Payment payment, Long teacherId) {
        Student student = payment.getStudent();
        BigDecimal sum = payment.getSum();
        BigDecimal overPaymentByStudent = student.getOverpayment();
        student.setOverpayment(overPaymentByStudent.add(sum));
        lessonCommandService.updatePaymentForStudent(student.getId(), teacherId);
    }


    public void delete(Long id) {
        Payment payment = paymentQueryService.getOneByIdOrThrow(id);
        Long teacherId = payment.getTeacher().getId();
        Long studentId = payment.getStudent().getId();
        List<Lesson> paidLessons = lessonQueryService.getPaidLessonsByStudent(teacherId, studentId);
        paidLessons.sort(Comparator.comparing(Lesson::getAddedPayment));
        BigDecimal sum = payment.getSum();
        int i = 0;
        BigDecimal addedPayment = paidLessons.get(i).getAddedPayment();
        while (sum.compareTo(addedPayment) >= 0) {
            Lesson lesson = paidLessons.get(i);
            lesson.setAddedPayment(BigDecimal.valueOf(0));
            lesson.setPaid(false);
            lesson.setRequiredPayment(true);
            lesson.setPayment(null);
            sum = sum.subtract(addedPayment);
            i++;
            if (paidLessons.size() <= i) {
                break;
            }
            addedPayment = paidLessons.get(i).getAddedPayment();

        }
        Student student = studentQueryService.getOneOrThrow(studentId);
        BigDecimal overpayment = student.getOverpayment();
        student.setOverpayment(overpayment.subtract(sum));
        paymentRepository.delete(payment);
    }

}
