package lt.techin.CarControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.CarController.CarControllerAdmin;
import lt.techin.dto.CarRequestDTO;
import lt.techin.model.Car;
import lt.techin.model.CarStatus;
import lt.techin.security.SecurityConfig;
import lt.techin.service.CarService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CarControllerAdminUpdateCarTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void updateCar_whenValid_thenReturnAnd200() throws Exception {
        // given
        CarRequestDTO carRequestDTO = new CarRequestDTO("BMW", "X55", 2020, BigDecimal.valueOf(75.00));

        Car car = new Car("Audi", "Model 1", 2015, CarStatus.AVAILABLE, List.of(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        Car updatedCar = new Car("BMW", "X55", 2020, CarStatus.AVAILABLE, List.of(), BigDecimal.valueOf(75.00));
        updatedCar.setId(1L);

        when(carService.existsCarById(1L)).thenReturn(true);
        when(carService.findCarById(1L)).thenReturn(Optional.of(car));
        when(carService.saveCar(any(Car.class))).thenReturn(updatedCar);

        //when
        mockMvc.perform(put("/api/cars/{id}", 1L) // Укажите правильный префикс пути, если отличается
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDTO)))
                // then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("brand").value("BMW"),
                        jsonPath("model").value("X55"),
                        jsonPath("year").value(2020),
                        jsonPath("dailyRentPrice").value(75.00)
                );

        Mockito.verify(carService, times(1)).existsCarById(1L);
        Mockito.verify(carService, times(1)).findCarById(1L);
        Mockito.verify(carService, times(1)).saveCar(any(Car.class));
    }
}
