package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Setter
@Getter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //  @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @ManyToOne
    private Slot slot;
    @ManyToOne(fetch = FetchType.EAGER)
    private Week week;
    @OneToOne(mappedBy = "lesson")
    private Rebooking rebooking;
    private boolean completed;
    private boolean cancelledByTeacher;
    private boolean cancelledByStudent;
    private boolean lastMinuteCancelled;
    private boolean rebooked;
    private boolean archived;
    private boolean paid;
    private boolean requiredPayment;
     private BigDecimal addedPayment;
    @ManyToOne
    private Payment payment;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
