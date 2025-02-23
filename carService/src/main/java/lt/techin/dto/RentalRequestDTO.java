package lt.techin.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lt.techin.model.Car;

import java.time.LocalDate;

public record RentalRequestDTO(@NotNull
                               long carId,
                               @NotNull
                               @Future
                               LocalDate startDate) {
}
