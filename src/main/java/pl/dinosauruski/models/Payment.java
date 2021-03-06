package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Teacher teacher;
    private BigDecimal sum;
    @OneToMany(mappedBy = "payment")
    private List<Lesson> paidLessons = new ArrayList<>();


}
