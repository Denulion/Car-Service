package lt.techin.repository;

import lt.techin.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByCarId(Long carId);

    List<Rental> findAllByUserId(Long userId);
}
