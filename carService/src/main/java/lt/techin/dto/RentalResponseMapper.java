package lt.techin.dto;

import lt.techin.model.Rental;

import java.util.List;

public class RentalResponseMapper {


    public static RentalResponseDTO toRentalResponseDTO(Rental rental) {
        return new RentalResponseDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart());
    }

    public static List<RentalResponseDTO> toRentalResponseDTOList(List<Rental> rentals) {
        return rentals.stream()
                .map(rental -> new RentalResponseDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                        CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart()))
                .toList();
    }
}
