package lt.techin.dto;

public record RentalResponseDTO(long id,
                                UserResponseDTO user,
                                CarResponseDTO car) {
}
