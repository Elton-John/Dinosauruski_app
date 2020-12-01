package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class YearForTeacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Teacher teacher;
    private Integer year;
    @org.hibernate.annotations.Type(type = "true_false")
    private Boolean isGenerated;
    @org.hibernate.annotations.Type(type = "true_false")
    private Boolean isArchived;
}
