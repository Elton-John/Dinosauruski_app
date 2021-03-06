package pl.dinosauruski.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Week {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Teacher teacher;
    private Integer numberOfWeek;
    private Integer year;
    private LocalDate mondayDate;
    @org.hibernate.annotations.Type(type = "true_false")
    private Boolean isGenerated;
    @org.hibernate.annotations.Type(type = "true_false")
    private Boolean isArchived;
    @OneToMany(mappedBy = "week")
    private List<Lesson> lessons = new ArrayList<>();

    public Boolean getGenerated() {
        return isGenerated;
    }

    public void setGenerated(Boolean generated) {
        isGenerated = generated;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Week week = (Week) o;

        if (!id.equals(week.id)) return false;
        if (!teacher.equals(week.teacher)) return false;
        if (!numberOfWeek.equals(week.numberOfWeek)) return false;
        return year.equals(week.year);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + teacher.hashCode();
        result = 31 * result + numberOfWeek.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
