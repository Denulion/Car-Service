package lt.techin.controller;

import jakarta.validation.Valid;
import lt.techin.dto.CarRequestMapper;
import lt.techin.dto.RentalRequestDTO;
import lt.techin.dto.RentalRequestMapper;
import lt.techin.dto.RentalResponseMapper;
import lt.techin.model.Rental;
import lt.techin.model.User;
import lt.techin.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rentals")
    public ResponseEntity<?> addRental(@Valid @RequestBody RentalRequestDTO rentalRequestDTO, Authentication authentication) {

        Rental rental = RentalRequestMapper.toRental(rentalRequestDTO);
        User user = (User) authentication.getPrincipal();
        rental.setUser(user);

        Rental savedRental = rentalService.save(rental);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedRental.getId())
                                .toUri())
                .body(RentalResponseMapper.toRentalResponseDTO(savedRental));
    }
}
