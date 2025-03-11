package lt.techin.controller.RentalController;

import io.swagger.v3.oas.annotations.Operation;
import lt.techin.dto.RentalResponseDTO;
import lt.techin.dto.RentalResponseMapper;
import lt.techin.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@RequestMapping("/api")
public class RentalControllerAdmin {

    private final RentalService rentalService;

    @Autowired
    public RentalControllerAdmin(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Get all rentals", description = "Retrieves all rentals, active and ended")
    @GetMapping("/rentals/history")
    public ResponseEntity<List<RentalResponseDTO>> getAllRentals() {
        return ResponseEntity.ok().body(RentalResponseMapper.toRentalResponseDTOList(rentalService.findAllRentals()));
    }
}
