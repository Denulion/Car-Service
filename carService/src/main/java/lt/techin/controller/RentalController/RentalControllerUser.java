package lt.techin.controller.RentalController;

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

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
@RequestMapping("/api")
public class RentalControllerUser {

    private final RentalService rentalService;

    @Autowired
    public RentalControllerUser(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/rentals/my")
    public ResponseEntity<List<RentalResponseDTO>> getActiveRentals(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(RentalResponseMapper.toRentalResponseDTOList(rentalService
                .findRentalsByUserId(user.getId()).stream().filter(i -> i.getRentalEnd() == null).toList()));
    }

    @GetMapping("/rentals/my/history")
    public ResponseEntity<List<RentalResponseDTO>> getInactiveRentals(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(RentalResponseMapper.toRentalResponseDTOList(rentalService
                .findRentalsByUserId(user.getId()).stream().filter(i -> i.getRentalEnd() != null).toList()));
    }

    @PostMapping("/rentals")
    public ResponseEntity<?> addRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO, Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        if (user.getRentals().stream().filter(rental -> rental.getRentalEnd() == null).toList().size() == 2) {
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
    public ResponseEntity<?> returnRentedCar(@PathVariable long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Rental rental = rentalService.findRentalsByCarId(id).stream().filter(i -> i.getRentalEnd() == null).findFirst().orElse(null);
        if (rental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not renting this car!");
        }
        if (rental.getUser().getId() == user.getId()) {
            rentalService.calculateTotalPriceAndReturnCar(rental);
            return ResponseEntity.ok().body(RentalEndMapper.toRentalEndDTO(rental));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not renting this car!");
        }
    }
}
