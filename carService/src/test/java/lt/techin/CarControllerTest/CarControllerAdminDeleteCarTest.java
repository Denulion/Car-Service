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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CarControllerAdminDeleteCarTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void deleteCar_whenCarExistsAndNotRented_thenReturn204() throws Exception {
        //given
        Car car = new Car("Audi", "A4", 2015, CarStatus.AVAILABLE, List.of(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        when(carService.existsCarById(1L)).thenReturn(true);
        when(carService.findCarById(1L)).thenReturn(Optional.of(car));
        doNothing().when(carService).deleteCarById(1L);

        //when
        mockMvc.perform(delete("/api/cars/{id}", 1L))
                //then
                .andExpect(status().isNoContent());

        Mockito.verify(carService, times(1)).deleteCarById(1L);
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void deleteCar_whenCarIsRented_thenReturnAnd400() throws Exception {
        //given
        Car car = new Car("Audi", "A4", 2015, CarStatus.RENTED, List.of(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        when(carService.existsCarById(1L)).thenReturn(true);
        when(carService.findCarById(1L)).thenReturn(Optional.of(car));

        //when
        mockMvc.perform(delete("/api/cars/{id}", 1L))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Car with this ID is currently rented!"));

        Mockito.verify(carService, times(0)).deleteCarById(1L);
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void deleteCar_whenCarDoesNotExist_thenReturn404() throws Exception {
        //given
        when(carService.existsCarById(1L)).thenReturn(false);

        //when
        mockMvc.perform(delete("/api/cars/{id}", 1L))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Car with this ID does not exist!"));

        Mockito.verify(carService, times(0)).deleteCarById(1L);
    }

    //unhappy path
    @Test
    void deleteCar_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/cars/{id}", 1L))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
