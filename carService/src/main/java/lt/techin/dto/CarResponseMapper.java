package lt.techin.dto;

import lt.techin.model.Car;

import java.util.List;

public class CarResponseMapper {


    public static CarResponseDTO toCarResponseDTO(Car car) {
        return new CarResponseDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getStatus(), car.getDailyRentPrice());
    }

    public static List<CarResponseDTO> toCarResponseDTOList(List<Car> cars) {
        return cars.stream()
                .map(i -> new CarResponseDTO(i.getId(), i.getBrand(), i.getModel(), i.getYear(), i.getStatus(), i.getDailyRentPrice()))
                .toList();
    }
}
