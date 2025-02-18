package lt.techin.dto;

import jakarta.validation.Valid;
import lt.techin.model.Rental;

public class RentalRequestMapper {


    public static Rental toRental(@Valid RentalRequestDTO rentalRequestDTO) {
        Rental rental = new Rental();

        rental.setCar(rentalRequestDTO.car());
        rental.setRentalStart(rentalRequestDTO.startDate());

        return rental;
    }
}
