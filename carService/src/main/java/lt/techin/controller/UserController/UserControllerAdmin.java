package lt.techin.controller.UserController;

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


    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.ok(UserResponseMapper.toUserResponseDTOList(userService.findAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable long id) {
        Optional<User> foundUser = userService.findUserById(id);
        if (foundUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserResponseMapper.toUserResponseDTO(foundUser.get()));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        if (userService.existsUserById(id)) {
            User userFromDB = userService.findUserById(id).get();

            UserRequestMapper.updateUserFromDTO(userFromDB, userRequestDTO);

            userService.saveUser(userFromDB);

            return ResponseEntity.ok(UserResponseMapper.toUserResponseDTO(userFromDB));
        }

        if (userService.existsUserByUsername(userRequestDTO.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A user with this username already exists!");
        }

        User savedUser = userService.saveUser(UserRequestMapper.toUser(userRequestDTO));

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .replacePath("/api/movies/{id}")
                                .buildAndExpand(savedUser.getId())
                                .toUri())
                .body(UserResponseMapper.toUserResponseDTO(savedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        if (!userService.existsUserById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}