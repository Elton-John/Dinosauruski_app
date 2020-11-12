package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  //  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate date;
    @ManyToOne
    private Slot slot;
//    @ManyToOne
//    private Teacher teacher;
    @ManyToOne
    private Student student;
    private boolean completed;
    private boolean cancelled;
    private boolean lastMinuteCancelled;
    private boolean transferred;
    private boolean archived;
    private boolean paid;
    private BigDecimal requiredPayment;
    @ManyToOne(fetch = FetchType.EAGER)
    private Week week;

}
