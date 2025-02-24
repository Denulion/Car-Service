package lt.techin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalEndDTO(long id,
                           UserResponseDTO user,
                           CarResponseDTO car,
                           LocalDate rentalStartDate,
                           LocalDate rentalEndDate,
                           BigDecimal totalPrice) {


}
