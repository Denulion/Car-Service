package lt.techin.dto;

import lt.techin.model.Rental;

import java.util.List;

public class RentalEndMapper {


    public static RentalEndDTO toRentalEndDTO(Rental rental) {
        return new RentalEndDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart(),
                rental.getRentalEnd(), rental.getPrice());
    }

    public static List<RentalEndDTO> toRentalEndDTOList(List<Rental> rentals) {
        return rentals.stream()
                .map(rental -> new RentalEndDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                        CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart(), rental.getRentalEnd(), rental.getPrice()))
                .toList();
    }
}
