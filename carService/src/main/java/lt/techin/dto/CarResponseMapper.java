package lt.techin.dto;

import lt.techin.model.Car;

public class CarResponseMapper {


    public static CarResponseDTO toCarResponseDTO(Car car) {
        return new CarResponseDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getStatus());
    }
}
