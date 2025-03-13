package lt.techin.service;

import lt.techin.model.CarStatus;
import lt.techin.model.Rental;
import lt.techin.model.User;
import lt.techin.repository.RentalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    private final UserService userService;

    public RentalService(RentalRepository rentalRepository, UserService userService) {
        this.rentalRepository = rentalRepository;
        this.userService = userService;
    }

    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }

    public void calculateTotalPriceAndReturnCar(Rental rental) {
        rental.setRentalEnd(LocalDate.now());

        long totalDays = Math.max(1, rental.getTotalDays());
        rental.setTotalPrice(BigDecimal.valueOf(totalDays).multiply(rental.getCar().getDailyRentPrice()));

        rental.getCar().setStatus(CarStatus.valueOf("AVAILABLE"));
    }

    public List<Rental> findRentalsByCarId(Long id) {
        return rentalRepository.findAllByCarId(id);
    }

    public List<Rental> findRentalsByUserId(Long id) {
        return rentalRepository.findAllByUserId(id);
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> findAllRentalsByUserId(long id) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        //added protection in case of exceptional cases (example: user is deleted by admin, but still has active bearer token)

        return user.getRentals().stream()
                .filter(rental -> rental.getRentalEnd() == null)
                .toList();
    }
}
