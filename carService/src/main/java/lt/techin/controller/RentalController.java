package lt.techin.controller;

import jakarta.validation.Valid;
import lt.techin.dto.*;
import lt.techin.model.CarStatus;
import lt.techin.model.Rental;
import lt.techin.model.User;
import lt.techin.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
    @PostMapping("/rentals")
    public ResponseEntity<?> addRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO, Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        if (user.getRentals().size() >= 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already have 2 cars rented!");
        }

        Rental rental = RentalRequestMapper.toRental(rentalRequestDTO, user);

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

    @PostMapping("/rentals/return/{id}")
    public ResponseEntity<?> returnRentedCar(@PathVariable long id) {

    }
}
