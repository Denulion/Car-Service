package lt.techin.controller.CarController;

import jakarta.validation.Valid;
import lt.techin.dto.*;
import lt.techin.model.Car;
import lt.techin.model.CarStatus;
import lt.techin.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
@RequestMapping("/api")
public class CarControllerAdmin {

    private final CarService carService;

    @Autowired
    public CarControllerAdmin(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarResponseDTO>> getCars() {
        return ResponseEntity.ok(CarResponseMapper.toCarResponseDTOList(carService.findAllCars()));
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<?> getCar(@PathVariable long id) {
        if (!carService.existsCarById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with this ID does not exist!");
        }
        Car foundCar = carService.findCarById(id).get();

        return ResponseEntity.ok(CarResponseMapper.toCarResponseDTO(foundCar));
    }

    @PostMapping("/cars")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarRequestDTO carRequestDTO) {
        Car car = CarRequestMapper.toCar(carRequestDTO);

        Car savedCar = carService.saveCar(car);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedCar.getId())
                                .toUri())
                .body(CarRequestMapper.toCarDTO(savedCar));
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<?> updateCar(@PathVariable long id, @Valid @RequestBody CarRequestDTO carRequestDTO) {
        if (carService.existsCarById(id)) {
            Car carFromDB = carService.findCarById(id).get();

            CarRequestMapper.updateCarFromDTO(carFromDB, carRequestDTO);

            carService.saveCar(carFromDB);

            return ResponseEntity.ok(CarResponseMapper.toCarResponseDTO(carFromDB));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This car doesn't exist!");
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable long id) {
        if (!carService.existsCarById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with this ID does not exist!");
        }

        if (carService.findCarById(id).get().getStatus().equals(CarStatus.valueOf("RENTED"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Car with this ID is currently rented!");
        }

        carService.deleteCarById(id);
        return ResponseEntity.noContent().build();
    }
}
