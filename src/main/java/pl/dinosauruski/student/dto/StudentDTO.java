package pl.dinosauruski.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    @NotBlank(message = "Pole nie może być puste.")
    private String name;
    @NotBlank(message = "Pole nie może być puste.")
    private String surname;
    @NotBlank(message = "Pole nie może być puste.")
    private String email;
    @DecimalMin(value = "0.0", inclusive = false, message = "Cena nie może być liczbą ujemną")
    @Digits(integer = 3, fraction = 2)
    @NotNull(message = "Pole nie może być puste")
    private BigDecimal priceForOneLesson;
    private BigDecimal overpayment;

    public String getFullName() {
        return this.name + " " + this.surname;
    }
}
