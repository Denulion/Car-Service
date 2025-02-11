package lt.techin.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleDTO(@NotBlank
                      String name) {
}
