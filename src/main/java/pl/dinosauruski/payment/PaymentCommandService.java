package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dinosauruski.lesson.LessonCommandService;
import pl.dinosauruski.lesson.LessonQueryService;
import pl.dinosauruski.lesson.dto.LessonPaymentDTO;
import pl.dinosauruski.models.Lesson;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.models.Student;
import pl.dinosauruski.payment.dto.PaymentDTO;
import pl.dinosauruski.teacher.TeacherQueryService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class PaymentCommandService {
    private PaymentRepository paymentRepository;
    private TeacherQueryService teacherQueryService;
    private PaymentQueryService paymentQueryService;
    private LessonCommandService lessonCommandService;
    private LessonQueryService lessonQueryService;

    public void create(PaymentDTO paymentDTO, Long teacherId) {
        Payment payment = new Payment();
        payment.setDate(paymentDTO.getDate().plusDays((long) 1));
        payment.setStudent(paymentDTO.getStudent());
        payment.setSum(paymentDTO.getSum());
        payment.setTeacher(teacherQueryService.getOneOrThrow(teacherId));
        Payment savedPayment = paymentRepository.save(payment);
        addPayment(savedPayment, teacherId);
    }

    public void addPayment(Payment payment, Long teacherId) {
        Student student = payment.getStudent();
        BigDecimal priceForOneLesson = student.getPriceForOneLesson();
        BigDecimal sum = payment.getSum();//
        BigDecimal overPaymentByStudent = paymentQueryService.getOverPayment(student.getId(), teacherId);
        setOverPaymentZero(student.getId(), teacherId);
        sum = sum.add(overPaymentByStudent);
        BigDecimal countPaidLessons = sum.divide(priceForOneLesson, 2, RoundingMode.HALF_UP);
        int quantityLessonsCanBePaid = countPaidLessons.intValue();
        List<LessonPaymentDTO> notPaidLessons = lessonQueryService.getNotPaidLessonsByStudent(teacherId, student.getId());
        if (notPaidLessons.size() < quantityLessonsCanBePaid) {
            int difference = quantityLessonsCanBePaid - notPaidLessons.size();
            BigDecimal overPayment = priceForOneLesson.multiply(BigDecimal.valueOf(difference));
            payment.setOverPayment(overPayment);
            quantityLessonsCanBePaid = quantityLessonsCanBePaid - difference;
        }
        for (int i = 0; i < quantityLessonsCanBePaid; i++) {
            LessonPaymentDTO lessonPaymentDTO = notPaidLessons.get(i);
            Lesson lesson = lessonQueryService.getOneOrThrow(lessonPaymentDTO.getId());
            lesson.setPayment(payment);
            lesson.setPaid(true);
            lesson.setRequiredPayment(false);
            lessonCommandService.saveLesson(lesson);
        }
    }

    public void update(Payment payment) {
        paymentRepository.save(payment);
    }

    public void updateByDto(PaymentDTO paymentDTO) {
        Payment payment = paymentQueryService.getOneByIdOrThrow(paymentDTO.getId());
        payment.setDate(paymentDTO.getDate());
        payment.setStudent(paymentDTO.getStudent());
        payment.setSum(paymentDTO.getSum());
        paymentRepository.save(payment);
    }

    public void delete(Long id) {
        Payment payment = paymentQueryService.getOneByIdOrThrow(id);
        List<Lesson> paidLessons = payment.getPaidLessons();
        paidLessons.forEach(lesson -> {
            lesson.setPaid(false);
            lesson.setRequiredPayment(true);
            lesson.setPayment(null);

        });
        paymentRepository.delete(payment);
    }

    public void setOverPaymentZero(Long studentId, Long teacherId) {
        List<Payment> overPayments = paymentRepository.findPaymentWhereOverPaymentByStudentAndTeacher(studentId, teacherId);
        overPayments.forEach(payment -> payment.setOverPayment(BigDecimal.valueOf(0)));
    }
}
