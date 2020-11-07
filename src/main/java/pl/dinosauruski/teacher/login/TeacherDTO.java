package pl.dinosauruski.teacher.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherDTO {
    private Long id;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeacherDTO that = (TeacherDTO) o;

        if (!id.equals(that.id)) return false;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
