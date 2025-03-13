package lt.techin.RentalControllerTest;

import lt.techin.controller.RentalController.RentalControllerUser;
import lt.techin.model.*;
import lt.techin.security.SecurityConfig;
import lt.techin.service.CarService;
import lt.techin.service.RentalService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RentalControllerUser.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class RentalControllerUserGetActiveRentalsTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RentalService rentalService;
    @MockitoBean
    private CarService carService;

    //happy path
    @Test
    void getActiveRentals_whenUser_thenReturnAnd200() throws Exception {
        //given
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                "password",
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_USER"))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        Car car2 = new Car("Honda", "Civic", 2019, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(45.00));
        car1.setId(1L);
        car2.setId(2L);

        Rental rental1 = new Rental(user, car1, LocalDate.of(2024, 3, 1), null, null);
        Rental rental2 = new Rental(user, car2, LocalDate.of(2024, 3, 5), null, null);
        rental1.setId(1L);
        rental2.setId(2L);

        List<Rental> rentals = List.of(rental1, rental2);

        given(rentalService.findRentalsByUserId(any())).willReturn(rentals);

        //when
        mockMvc.perform(get("/api/rentals/my"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("[0].car.model").value("Camry"))
                .andExpect(jsonPath("[0].car.year").value(2020))
                .andExpect(jsonPath("[0].rentalStartDate").value("2024-03-01"))

                .andExpect(jsonPath("[1].car.brand").value("Honda"))
                .andExpect(jsonPath("[1].car.model").value("Civic"))
                .andExpect(jsonPath("[1].car.year").value(2019))
                .andExpect(jsonPath("[1].rentalStartDate").value("2024-03-05"));

        Mockito.verify(rentalService, times(1)).findRentalsByUserId(any());
    }

    //unhappy path
    @Test
    void getActiveRentals_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        given(rentalService.findRentalsByUserId(any())).willReturn(List.of());

        //when
        mockMvc.perform(get("/api/rentals/my"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(rentalService, times(0)).findRentalsByUserId(any());
    }

    //unhappy path
    @Test
    void getActiveRentals_whenActiveRentalsIsEmpty_thenReturnEmptyListAnd200() throws Exception {
        //given
        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                "password",
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_USER"))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        given(rentalService.findRentalsByUserId(any())).willReturn(List.of());

        //when
        mockMvc.perform(get("/api/rentals/my"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}
