package lt.techin.dto;

import lt.techin.model.CarStatus;

import java.math.BigDecimal;

public record CarResponseDTO(long id,
                             String brand,
                             String model,
                             int year,
                             CarStatus status,
                             BigDecimal dailyRentPrice) {
}
