package lt.techin.dto;

import lt.techin.model.Car;

import java.util.List;

public class CarMapper {
    public static List<CarDTO> toCarDTOList(List<Car> carList) {
        return carList.stream()
                .map(car -> new CarDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getStatus()))
                .toList();
    }

    public static Car toCar(CarDTO carDTO) {
        Car car = new Car();

        updateCarFromDTO(car, carDTO);

        return car;
    }

    public static CarDTO toCarDTO(Car car) {
        return new CarDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getStatus());
    }

    public static void updateCarFromDTO(Car car, CarDTO carDTO) {
        car.setId(carDTO.id());
        car.setBrand(carDTO.brand());
        car.setModel(carDTO.model());
        car.setYear(carDTO.year());
        car.setStatus(carDTO.status());
    }
}
