package pl.dinosauruski.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pl.dinosauruski.models.Student;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Pole nie może być puste")
    @Past(message = "Data wpłaty nie może być datą z przyszłości")
    private LocalDate date;
    @NotNull
    private Student student;
    @DecimalMin(value = "0.0", inclusive = false, message = "Suma nie może być liczbą ujemną")
    @Digits(integer = 3, fraction = 2)
    @NotNull(message = "Pole nie może być puste")
    private BigDecimal sum;

}
