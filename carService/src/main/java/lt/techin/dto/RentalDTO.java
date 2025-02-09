package lt.techin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalDTO(@NotNull
                        Long id,
                        @NotNull(message = "User ID cannot be null")
                        Long userId,
                        @NotNull(message = "Car ID cannot be null")
                        Long carId,
                        @NotNull(message = "Rental start date cannot be null")
                        @FutureOrPresent(message = "Rental start date must be today or in the future")
                        LocalDate rentalStart,
                        @NotNull(message = "Rental end date cannot be null")
                        @Future(message = "Rental end date must be in the future")
                        LocalDate rentalEnd,
                        @NotNull(message = "Price cannot be null")
                        @DecimalMin(value = "0.00", message = "Price must be positive")
                        BigDecimal price) {
}
