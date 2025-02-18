package lt.techin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lt.techin.model.CarStatus;
import org.hibernate.validator.constraints.Length;

public record CarDTO(Long id,
                     @NotBlank
                     @Length(min = 3, max = 255, message = "Car name is too long or too short!")
                     String brand,
                     @NotBlank
                     @Length(min = 3, max = 255, message = "Car name is too long or too short!")
                     String model,
                     @NotBlank
                     @Min(value = 1950, message = "Invalid date")
                     int year,
                     //!!!!!!!
                     CarStatus status) {
}
