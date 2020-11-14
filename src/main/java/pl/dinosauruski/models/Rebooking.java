package pl.dinosauruski.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Rebooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Student notRegularStudent;
    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    private Lesson lesson;
}
