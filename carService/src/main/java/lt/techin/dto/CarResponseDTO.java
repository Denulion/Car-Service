package lt.techin.dto;

import lt.techin.model.CarStatus;

public record CarResponseDTO(long id,
                             String brand,
                             String model,
                             int year,
                             CarStatus status) {
}
