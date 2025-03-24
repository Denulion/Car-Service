package lt.techin.CarControllerTest;

import lt.techin.controller.CarController.CarControllerUser;
import lt.techin.model.Car;
import lt.techin.model.CarStatus;
import lt.techin.model.Role;
import lt.techin.model.User;
import lt.techin.security.SecurityConfig;
import lt.techin.service.CarService;
import org.hamcrest.Matchers;
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
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarControllerUser.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class CarControllerUserGetTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER")
    void getAvailableRentals_whenValid_thenReturnAnd200() throws Exception {
        //given
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(50.00));
        Car car2 = new Car("Honda", "Civic", 2019, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(45.00));
        car1.setId(1L);
        car2.setId(2L);

        when(carService.findAllCars()).thenReturn(List.of(car1, car2));

        //when
        mockMvc.perform(get("/api/cars/available"))
                //then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("[0].id").value(1L),
                        jsonPath("[0].brand").value("Toyota"),
                        jsonPath("[0].model").value("Camry"),
                        jsonPath("[0].year").value(2020),
                        jsonPath("[0].status").value("AVAILABLE"),
                        jsonPath("[0].dailyRentPrice").value(50.00),

                        jsonPath("[1].id").value(2L),
                        jsonPath("[1].brand").value("Honda"),
                        jsonPath("[1].model").value("Civic"),
                        jsonPath("[1].year").value(2019),
                        jsonPath("[1].status").value("AVAILABLE"),
                        jsonPath("[1].dailyRentPrice").value(45.00)
                );

        Mockito.verify(carService, times(1)).findAllCars();
    }

    //unhappy path
    @Test
    void getAvailableRentals_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given

        //when
        mockMvc.perform(get("/api/cars/available"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(carService, times(0)).findAllCars();
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER")
    void getAvailableRentals_whenNoCarsAvailable_thenReturnAnd200() throws Exception {
        //given
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        when(carService.findAllCars()).thenReturn(List.of());

        //when
        mockMvc.perform(get("/api/cars/available"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}
