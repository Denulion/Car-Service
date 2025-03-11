package lt.techin.controller.UserController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lt.techin.dto.UserRequestDTO;
import lt.techin.dto.UserRequestMapper;
import lt.techin.dto.UserResponseDTO;
import lt.techin.dto.UserResponseMapper;
import lt.techin.model.User;
import lt.techin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@RequestMapping("/api")
public class UserControllerAdmin {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserControllerAdmin(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Get all existing users", description = "Retrieves all users from database")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.ok(UserResponseMapper.toUserResponseDTOList(userService.findAllUsers()));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable long id) {
        Optional<User> foundUser = userService.findUserById(id);
        if (foundUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserResponseMapper.toUserResponseDTO(foundUser.get()));
    }

    @Operation(summary = "Update user by ID", description = "Updates a user by their unique ID")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        if (userService.existsUserById(id)) {

            User userFromDB = userService.findUserById(id).get();

            UserRequestMapper.updateUserFromDTO(userFromDB, userRequestDTO);

            userFromDB.setPassword(passwordEncoder.encode(userRequestDTO.password()));

            userService.saveUser(userFromDB);

            return ResponseEntity.ok(UserResponseMapper.toUserResponseDTO(userFromDB));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user does not exist!");
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        if (!userService.existsUserById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}