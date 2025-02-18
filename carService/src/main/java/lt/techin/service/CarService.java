package lt.techin.service;

import lt.techin.model.Car;
import lt.techin.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    public Car saveCar(Car car){return carRepository.save(car);}

    public List<Car> findAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> findCarById(long id) {
        return carRepository.findById(id);
    }

    public boolean existsCarById(long id) {
        return carRepository.existsById(id);
    }


    public void deleteCarById(long id) {
        carRepository.deleteById(id);
    }
}
