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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CarControllerAdminPostTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void addCar_whenValidRequest_thenReturnAnd201() throws Exception {
        //given
        CarRequestDTO carRequestDTO = new CarRequestDTO("Audi", "Model 1", 2015, BigDecimal.valueOf(50.00));

        Car car = new Car("Audi", "Model 1", 2015, CarStatus.AVAILABLE, List.of(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        when(carService.saveCar(any(Car.class))).thenReturn(car);

        // when
        mockMvc.perform(post("/api/cars") // Укажите правильный путь, если он отличается
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDTO)))
                // then
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("brand").value("Audi"),
                        jsonPath("model").value("Model 1"),
                        jsonPath("year").value(2015),
                        jsonPath("dailyRentPrice").value(50.00)
                );

        Mockito.verify(carService, times(1)).saveCar(any(Car.class));
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void addCar_whenInvalidRequest_thenReturnAnd400() throws Exception {
        //given
        CarRequestDTO carRequestDTO = new CarRequestDTO("Au", "Model аыафп", 1949, BigDecimal.valueOf(0.00));

        //when
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDTO)))
                //then
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("brand").value("Brand must be between 3 and 255 characters!"),
                        jsonPath("model").value("Model can only contain letters, numbers, spaces, hyphens, slashes, apostrophes, commas, and periods."),
                        jsonPath("year").value("Year must be 1950 or later"),
                        jsonPath("dailyRentPrice").value("Daily rent price must be greater than 0 and have up to 10 digits with 2 decimal places!")
                );

        Mockito.verify(carService, times(0)).saveCar(any(Car.class));
    }

    //unhappy path
    @Test
    void addCar_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        //when
        mockMvc.perform(post("/api/cars"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
