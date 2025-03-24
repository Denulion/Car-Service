package lt.techin.CarControllerTest;

import lt.techin.controller.CarController.CarControllerAdmin;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CarControllerAdminGetCarByIdTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getCarById_whenValid_thenReturnAnd200() throws Exception {
        //given
        Car car = new Car("Toyota", "Camry", 2020, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        when(carService.existsCarById(1L)).thenReturn(true);
        when(carService.findCarById(1L)).thenReturn(Optional.of(car));

        //when
        mockMvc.perform(get("/api/cars/{id}", 1L))
                //then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("id").value(1L),
                        jsonPath("brand").value("Toyota"),
                        jsonPath("model").value("Camry"),
                        jsonPath("year").value(2020),
                        jsonPath("status").value("AVAILABLE"),
                        jsonPath("dailyRentPrice").value(50.00)
                );

        Mockito.verify(carService, times(1)).existsCarById(1L);
        Mockito.verify(carService, times(1)).findCarById(1L);
    }

    //unhappy path
    @Test
    void getCarById_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/cars/1"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(carService, times(0)).findCarById(1L);
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getCarById_whenCarDoesNotExist_thenReturnAnd404() throws Exception {
        //given
        when(carService.findCarById(1L)).thenReturn(Optional.empty());

        //when
        mockMvc.perform(get("/api/cars/1"))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Car with this ID does not exist!"));

        Mockito.verify(carService, times(1)).existsCarById(1L);
        Mockito.verify(carService, times(0)).findCarById(1L);
    }
}
