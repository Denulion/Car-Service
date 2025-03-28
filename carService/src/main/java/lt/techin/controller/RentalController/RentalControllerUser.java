package lt.techin.controller.RentalController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lt.techin.dto.*;
import lt.techin.model.Car;
import lt.techin.model.CarStatus;
import lt.techin.model.Rental;
import lt.techin.service.CarService;
import lt.techin.service.RentalService;
import lt.techin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
@RequestMapping("/api")
public class RentalControllerUser {

    private final RentalService rentalService;
    private final CarService carService;
    private final UserService userService;

    @Autowired
    public RentalControllerUser(RentalService rentalService, CarService carService, UserService userService) {
        this.rentalService = rentalService;
        this.carService = carService;
        this.userService = userService;
    }

    @Operation(summary = "Get all active rentals for current user", description = "Retrieves all active rentals for currently authenticated user")
    @GetMapping("/rentals/my")
    public ResponseEntity<List<RentalResponseDTO>> getActiveRentals(Authentication authentication) {
        return ResponseEntity.ok().body(RentalResponseMapper.toRentalResponseDTOList(rentalService
                .findRentalsByUserId(((Jwt) authentication.getPrincipal()).getClaim("user_id")).stream().filter(i -> i.getRentalEnd() == null).toList()));
    }

    @Operation(summary = "Get all ended rentals for current user", description = "Retrieves all ended rentals for currently authenticated user")
    @GetMapping("/rentals/my/history")
    public ResponseEntity<List<RentalEndDTO>> getInactiveRentals(Authentication authentication) {
        return ResponseEntity.ok().body(RentalEndMapper.toRentalEndDTOList(rentalService
                .findRentalsByUserId(((Jwt) authentication.getPrincipal()).getClaim("user_id")).stream().filter(i -> i.getRentalEnd() != null).toList()));
    }

    @Operation(summary = "Post new rental", description = "Posts new rental by Car unique ID and with start date (should be in future)")
    @PostMapping("/rentals")
    public ResponseEntity<?> addRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO, Authentication authentication) {

        long userId = ((Jwt) authentication.getPrincipal()).getClaim("user_id");

        if (rentalService.findAllRentalsByUserId(userId).size() >= 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already have 2 cars rented!");
        }

        Car car = carService.findCarById(rentalRequestDTO.carId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        Rental rental = RentalRequestMapper.toRental(rentalRequestDTO, userService.findUserById(userId).get(), car);

        if (rental.getCar().getStatus().equals(CarStatus.valueOf("RENTED"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This car is already rented!");
        }

        Rental savedRental = rentalService.save(rental);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedRental.getId())
                                .toUri())
                .body(RentalResponseMapper.toRentalResponseDTO(savedRental));
    }

    @Operation(summary = "Return car by ID", description = "Returns a car by it's unique ID and a total price for the rent")
    @PostMapping("/rentals/return/{id}")
    public ResponseEntity<?> returnRentedCar(@PathVariable long id, Authentication authentication) {
        long userId = ((Jwt) authentication.getPrincipal()).getClaim("user_id");

        Rental rental = rentalService.findRentalsByCarId(id).stream().filter(i -> i.getRentalEnd() == null).findFirst().orElse(null);
        if (rental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not renting this car!");
        }
        if (rental.getUser().getId() == userId) {
            rentalService.calculatePriceAndReturnCar(rental);
            return ResponseEntity.ok().body(RentalEndMapper.toRentalEndDTO(rental));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not renting this car!");
        }
    }
}
