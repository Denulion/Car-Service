package lt.techin.dto;

import jakarta.validation.Valid;
import lt.techin.model.Rental;
import lt.techin.model.User;

public class RentalRequestMapper {


    public static Rental toRental(@Valid RentalRequestDTO rentalRequestDTO, User user) {
        Rental rental = new Rental();

        rental.setCar(rentalRequestDTO.car());
        rental.setRentalStart(rentalRequestDTO.startDate());
        rental.setUser(user);

        return rental;
    }
}
