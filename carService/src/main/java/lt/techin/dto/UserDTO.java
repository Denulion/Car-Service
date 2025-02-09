package lt.techin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserDTO(@NotBlank
                      @Length(min = 2, max = 255)
                      String username,
                      @NotBlank
                      @Length(min = 2, max = 255)
                      String password,
                      @NotEmpty
                      List<RoleDTO> roles,
                      @NotEmpty
                      List<RentalDTO> rentals) {
}
