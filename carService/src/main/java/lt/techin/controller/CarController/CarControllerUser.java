package lt.techin.controller.CarController;

import lt.techin.dto.CarResponseDTO;
import lt.techin.dto.CarResponseMapper;
import lt.techin.model.CarStatus;
import lt.techin.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_USER')")
@RequestMapping("/api")
public class CarControllerUser {

    private final CarService carService;

    @Autowired
    public CarControllerUser(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars/available")
    public ResponseEntity<List<CarResponseDTO>> getAvailableCars() {
        return ResponseEntity.ok(CarResponseMapper.toCarResponseDTOList(carService.findAllCars().stream()
                .filter(car -> car.getStatus().equals(CarStatus.valueOf("AVAILABLE")))
                .toList()));
    }
}
