package pl.dinosauruski.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRegistrationFormDTO {
    private Long id;
    @NotBlank(message = "Pole nie możeeee być puste.")
    private String name;
    @NotBlank(message = "Pole nie możeee być puste.")
    private String surname;
    private String nickname;
    @NotBlank(message = "Pole nie możeeee być puste.")
    @Size(min = 3, message = "Minimum 8 znaków.")
    private String password;
    @NotBlank(message = "Pole nie może być puste.")
    private String repeatPassword;
    @NotBlank(message = "Pole nie może być puste.")
    @Email
    private String email;


}
