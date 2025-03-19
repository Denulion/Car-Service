package lt.techin.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CarRequestDTO(@NotBlank(message = "Brand must be provided and be not empty!")
                            @Length(min = 3, max = 255,
                                    message = "Brand must be between 3 and 255 characters!")
                            @Pattern(regexp = "^[A-Za-z0-9\\s\\-\\/'.,]+$",
                                    message = "Brand can only contain letters, numbers, spaces, hyphens, slashes, apostrophes, commas, and periods."
                            )
                            String brand,
                            @NotBlank(message = "Model must be provided and be not empty!")
                            @Length(min = 3, max = 255,
                                    message = "Model must be between 3 and 255 characters!")
                            @Pattern(regexp = "^[A-Za-z0-9\\s\\-\\/'.,]+$",
                                    message = "Model can only contain letters, numbers, spaces, hyphens, slashes, apostrophes, commas, and periods."
                            )
                            String model,
                            @Min(value = 1950,
                                    message = "Year must be 1950 or later")
                            int year,
                            @DecimalMin(value = "0.0",
                                    inclusive = false,
                                    message = "Daily rent price must be greater than 0 and have up to 10 digits with 2 decimal places!")
                            @Digits(integer = 10, fraction = 2,
                                    message = "Daily rent price must have up to 10 digits and 2 decimal places!")
                            BigDecimal dailyRentPrice) {
}
