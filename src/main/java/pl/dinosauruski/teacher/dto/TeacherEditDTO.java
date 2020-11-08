package pl.dinosauruski.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherEditDTO {
    private Long id;
    @NotBlank(message = "Pole nie może być puste.")
    private String name;
    @NotBlank(message = "Pole nie może być puste.")
    private String surname;
    private String nickname;
}
