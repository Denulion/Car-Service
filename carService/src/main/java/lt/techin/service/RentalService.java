package lt.techin.service;

import lt.techin.model.Rental;
import lt.techin.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }

    public BigDecimal calculateTotalPrice(Rental rental) {
        rental.setRentalEnd(LocalDate.now());
        rental.setTotalPrice(BigDecimal.valueOf(rental.getTotalDays()).multiply(rental.getCar().getDailyRentPrice()));
    }
}
