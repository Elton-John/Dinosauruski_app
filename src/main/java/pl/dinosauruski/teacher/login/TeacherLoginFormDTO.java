package pl.dinosauruski.teacher.login;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class TeacherLoginFormDTO {
    @NotBlank(message = "Pole nie może być puste")
    private String email;
    @NotBlank(message = "Pole nie może być puste")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeacherLoginFormDTO that = (TeacherLoginFormDTO) o;

        if (!email.equals(that.email)) return false;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
