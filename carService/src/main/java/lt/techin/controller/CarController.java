package lt.techin.controller;

import jakarta.validation.Valid;
import lt.techin.dto.*;
import lt.techin.model.Car;
import lt.techin.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getCars() {
        return ResponseEntity.ok(CarMapper.toCarDTOList(carService.findAllCars()));
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable long id) {
        Optional<Car> foundCar = carService.findCarById(id);
        if (foundCar.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CarMapper.toCarDTO(foundCar.get()));
    }

    @PostMapping("/cars")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDTO carDTO) {
        Car car = CarMapper.toCar(carDTO);

        Car savedCar = carService.saveCar(car);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedCar.getId())
                                .toUri())
                .body(CarMapper.toCarDTO(savedCar));
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<?> updateCar(@PathVariable long id, @Valid @RequestBody CarDTO carDTO) {
        if (carService.existsCarById(id)) {
            Car carFromDB = carService.findCarById(id).get();

            CarMapper.updateCarFromDTO(carFromDB, carDTO);

            carService.saveCar(carFromDB);

            return ResponseEntity.ok(carDTO);
        }

        Car savedCar = carService.saveCar(CarMapper.toCar(carDTO));

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest()
                                .replacePath("/api/cars/{id}")
                                .buildAndExpand(savedCar.getId())
                                .toUri())
                .body(CarMapper.toCarDTO(savedCar));
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable long id) {
        if (!carService.existsCarById(id)) {
            return ResponseEntity.notFound().build();
        }

        carService.deleteCarById(id);
        return ResponseEntity.noContent().build();
    }
}
