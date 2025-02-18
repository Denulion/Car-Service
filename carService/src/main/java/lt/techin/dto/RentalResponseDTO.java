package lt.techin.dto;

import java.time.LocalDate;

public record RentalResponseDTO(long id,
                                UserResponseDTO user,
                                CarResponseDTO car,
                                LocalDate rentalStartDate) {
}
