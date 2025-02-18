package lt.techin.service;

import lt.techin.model.Rental;
import lt.techin.repository.RentalRepository;
import org.springframework.stereotype.Service;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }
}
