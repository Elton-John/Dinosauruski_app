package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LessonPaymentDTO {
    private Long id;
    private boolean paid;
    private BigDecimal requiredPayment;
}
