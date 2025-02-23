package lt.techin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lt.techin.model.CarStatus;

import java.math.BigDecimal;

public record CarResponseDTO(long id,
                             String brand,
                             String model,
                             int year,
                             CarStatus status,
                             @DecimalMin(value = "0.0", inclusive = false)
                             @Digits(integer = 10, fraction = 2)
                             BigDecimal dailyRentPrice) {
}
