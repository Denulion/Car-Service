package lt.techin.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lt.techin.model.Car;

import java.time.LocalDate;

public record RentalRequestDTO(@NotNull(message = "Car ID must be provided!")
                               long carId,
                               @NotNull(message = "Start date must be provided!")
                               @Future(message = "You can rent a car only from the next day")
                               LocalDate startDate) {
}
