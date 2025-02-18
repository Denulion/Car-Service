package lt.techin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserPostResponseDTO(@NotBlank
                                 @Length(min = 2, max = 255)
                                 String username,
                                 @NotEmpty
                                 List<RoleDTO> roles) {


}
