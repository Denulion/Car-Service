package lt.techin.controller;

import jakarta.validation.Valid;
import lt.techin.dto.UserRequestDTO;
import lt.techin.dto.UserRequestMapper;
import lt.techin.dto.UserResponseMapper;
import lt.techin.model.User;
import lt.techin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserRequestDTO>> getUsers() {
        return ResponseEntity.ok(UserRequestMapper.toUserDTOList(userService.findAllUsers()));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserRequestDTO> getUser(@PathVariable long id) {
        Optional<User> foundUser = userService.findUserById(id);
        if (foundUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserRequestMapper.toUserDTO(foundUser.get()));
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are already registered!");
        }

        if (userService.existsUserByUsername(userRequestDTO.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this username already exists!");
        }

        User user = UserRequestMapper.toUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userService.saveUser(user);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedUser.getId())
                                .toUri())
                .body(UserResponseMapper.toUserResponseDTO(savedUser));
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        if (!userService.existsUserById(id)) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}