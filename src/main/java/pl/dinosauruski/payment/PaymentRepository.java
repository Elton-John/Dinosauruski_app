package pl.dinosauruski.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findOneById(@Param("id") Long id);

    @Query("SELECT p FROM Payment p WHERE p.teacher.id = :id")
    List<Payment> findAllByTeacherId(@Param("id") Long id);

    @Query("SELECT new pl.dinosauruski.payment.dto.PaymentDTO(p.id, p.date, p.student, p.sum) FROM Payment p WHERE p.id = :id")
    Optional<PaymentDTO> findOneDtoById(@Param("id") Long id);


}
