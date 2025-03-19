package lt.techin.dto;

import jakarta.validation.constraints.NotNull;

public record RoleDTO(@NotNull(message = "Role ID must not be null!")
                      long id) {
}
