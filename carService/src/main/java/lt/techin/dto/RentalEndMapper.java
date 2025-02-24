package lt.techin.dto;

import lt.techin.model.Rental;

public class RentalEndMapper {


    public static RentalEndDTO toRentalEndDTO(Rental rental) {
        return new RentalEndDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart(),
                rental.getRentalEnd(), rental.getTotalPrice());
    }
}
