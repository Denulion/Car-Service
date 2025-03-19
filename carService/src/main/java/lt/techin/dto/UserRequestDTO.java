package lt.techin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserRequestDTO(@NotBlank(message = "Username must not be blank!")
                             @Length(min = 3, max = 255,
                                     message = "Username must be between 3 and 255 characters!")
                             String username,
                             @NotBlank(message = "Password must not be blank!")
                             @Length(min = 3, max = 255,
                                     message = "Password must be between 3 and 255 characters!")
                             @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*-_+=]+$",
                                     message = "Password must contain only letters, numbers, and common symbols (!@#$%^&*-_+=), no spaces!")
                             String password,
                             @NotEmpty(message = "At least one role must be specified!")
                             List<RoleDTO> roles) {
}
