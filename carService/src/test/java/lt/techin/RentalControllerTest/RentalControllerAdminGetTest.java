package lt.techin.RentalControllerTest;

import lt.techin.controller.RentalController.RentalControllerAdmin;
import lt.techin.dto.*;
import lt.techin.model.*;
import lt.techin.security.SecurityConfig;
import lt.techin.service.RentalService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RentalControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class RentalControllerAdminGetTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RentalService rentalService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getAllRentals_whenAdmin_thenReturnAnd200() throws Exception {
        // given
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user1 = new User("username1", "password1", List.of(role), List.of());
        User user2 = new User("username2", "password2", List.of(role), List.of());

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(50.00));
        Car car2 = new Car("Honda", "Civic", 2019, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(45.00));

        car1.setId(1L);
        car2.setId(2L);

        Rental rental1 = new Rental(user1, car1, LocalDate.of(2024, 3, 1), null, null);
        Rental rental2 = new Rental(user2, car2, LocalDate.of(2024, 3, 5), null, null);

        rental1.setId(1L);
        rental2.setId(2L);

        List<Rental> rentals = List.of(rental1, rental2);

        given(rentalService.findAllRentals()).willReturn(rentals);

        // when
        mockMvc.perform(get("/api/rentals/history"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].user.username").value("username1"))
                .andExpect(jsonPath("[0].user.roles[0].name").value("ROLE_USER"))
                .andExpect(jsonPath("[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("[0].car.model").value("Camry"))
                .andExpect(jsonPath("[0].car.year").value(2020))
                .andExpect(jsonPath("[0].car.status").value("AVAILABLE"))
                .andExpect(jsonPath("[0].car.dailyRentPrice").value(50.00))
                .andExpect(jsonPath("[0].rentalStartDate").value("2024-03-01"))

                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].user.username").value("username2"))
                .andExpect(jsonPath("[1].user.roles[0].name").value("ROLE_USER"))
                .andExpect(jsonPath("[1].car.brand").value("Honda"))
                .andExpect(jsonPath("[1].car.model").value("Civic"))
                .andExpect(jsonPath("[1].car.year").value(2019))
                .andExpect(jsonPath("[1].car.status").value("RENTED"))
                .andExpect(jsonPath("[1].car.dailyRentPrice").value(45.00))
                .andExpect(jsonPath("[1].rentalStartDate").value("2024-03-05"));

        Mockito.verify(rentalService, times(1)).findAllRentals();
    }

}
