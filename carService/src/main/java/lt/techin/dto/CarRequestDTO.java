package lt.techin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CarRequestDTO(@NotBlank
                            @Length(min = 3, max = 255, message = "Car name is too long or too short!")
                            String brand,
                            @NotBlank
                            @Length(min = 3, max = 255, message = "Car name is too long or too short!")
                            String model,
                            @Min(value = 1950, message = "Invalid date")
                            int year,
                            @DecimalMin(value = "0.0", inclusive = false)
                            @Digits(integer = 10, fraction = 2)
                            BigDecimal dailyRentPrice) {
}
