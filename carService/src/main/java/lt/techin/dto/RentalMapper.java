package lt.techin.dto;

import lt.techin.model.Rental;
import lt.techin.model.User;

import java.util.List;

public class RentalMapper {
    public static List<RentalDTO> toRentalDTOList(User user) {
        return user.getRentals().stream()
                .map(RentalMapper::toRentalDTO)
                .toList();
    }

    private static RentalDTO toRentalDTO(Rental rental) {
        return new RentalDTO(
                rental.getId(),
                rental.getUser().getId(),
                rental.getCar().getId(),
                rental.getRentalStart(),
                rental.getRentalEnd(),
                rental.getPrice()
        );
    }

    public static List<Rental> toRentalListFromDTO(List<RentalDTO> rentalDTOList) {
        return rentalDTOList.stream().map(RentalMapper::toRental).toList();
    }

    private static Rental toRental(RentalDTO rentalDTO) {
        Rental rental = new Rental();

        rental.setId(rentalDTO.id());

        // !!!
        rental.setRentalStart(rentalDTO.rentalStart());
        rental.setRentalEnd(rentalDTO.rentalEnd());
        rental.setPrice(rentalDTO.price());

        return rental;
    }
}
