package pl.dinosauruski.rebooking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dinosauruski.models.Rebooking;
import pl.dinosauruski.rebooking.dto.RebookingDTO;

import java.util.Optional;

@Repository
public interface RebookingRepository extends JpaRepository<Rebooking, Long> {

    @Query("SELECT new pl.dinosauruski.rebooking.dto.RebookingDTO(r.id, r.notRegularStudent) FROM Rebooking r WHERE r.id = :id ")
    Optional<RebookingDTO> findOneDtoById(@Param("id") Long lessonId);

    Optional<Rebooking> findOneById(Long id);
}
