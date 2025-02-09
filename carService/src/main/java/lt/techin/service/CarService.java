package lt.techin.service;

import lt.techin.model.Car;
import lt.techin.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    public Car saveCar(Car car){return carRepository.save(car);}
}
