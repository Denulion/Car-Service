package lt.techin.dto;

import lt.techin.model.Car;

import java.util.List;

public class CarRequestMapper {
    public static List<CarRequestDTO> toCarDTOList(List<Car> carList) {
        return carList.stream()
                .map(car -> new CarRequestDTO(car.getBrand(), car.getModel(), car.getYear(), car.getDailyRentPrice()))
                .toList();
    }

    public static Car toCar(CarRequestDTO carRequestDTO) {
        Car car = new Car();

        updateCarFromDTO(car, carRequestDTO);

        return car;
    }

    public static CarRequestDTO toCarDTO(Car car) {
        return new CarRequestDTO(car.getBrand(), car.getModel(), car.getYear(), car.getDailyRentPrice());
    }

    public static void updateCarFromDTO(Car car, CarRequestDTO carRequestDTO) {

        car.setBrand(carRequestDTO.brand());
        car.setModel(carRequestDTO.model());
        car.setYear(carRequestDTO.year());
        car.setDailyRentPrice(carRequestDTO.dailyRentPrice());
    }
}
