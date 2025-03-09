package lt.techin.dto;

import jakarta.validation.Valid;
import lt.techin.model.Car;
import lt.techin.model.Rental;
import lt.techin.model.User;

public class RentalRequestMapper {


    public static Rental toRental(@Valid RentalRequestDTO rentalRequestDTO, User user, Car car) {
        Rental rental = new Rental();

        rental.setCar(car);
        rental.setRentalStart(rentalRequestDTO.startDate());
        rental.setUser(user);

        return rental;
    }
}
