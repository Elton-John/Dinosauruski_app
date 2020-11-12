package pl.dinosauruski.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
}
