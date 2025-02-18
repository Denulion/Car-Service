package lt.techin.dto;

import lt.techin.model.Rental;
import lt.techin.model.User;

import java.util.List;

public class RentalResponseMapper {


    public static RentalResponseDTO toRentalResponseDTO(Rental rental) {
        return new RentalResponseDTO(rental.getId(), UserResponseMapper.toUserResponseDTO(rental.getUser()),
                CarResponseMapper.toCarResponseDTO(rental.getCar()), rental.getRentalStart());
    }

//    public static List<RentalResponseDTO> toRentalResponseDTOList(User user) {
//        return
//    }
}
