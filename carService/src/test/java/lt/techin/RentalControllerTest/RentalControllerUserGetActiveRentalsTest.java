package lt.techin.RentalControllerTest;

import lt.techin.controller.RentalController.RentalControllerUser;
import lt.techin.model.*;
import lt.techin.security.SecurityConfig;
import lt.techin.service.CarService;
import lt.techin.service.RentalService;
import lt.techin.service.UserService;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @MockitoBean
    private UserService userService;

    //happy path
    @Test
    void getActiveRentals_whenUser_thenReturnAnd200() throws Exception {
        //given
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", 1L);
        claims.put("scope", "SCOPE_ROLE_USER");
        claims.put("sub", "username");

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims
        );
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_USER")),
                "username"
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        Car car2 = new Car("Honda", "Civic", 2019, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(45.00));
        car1.setId(1L);
        car2.setId(2L);

        Rental rental1 = new Rental(user, car1, LocalDate.of(2024, 3, 1), null, null);
        Rental rental2 = new Rental(user, car2, LocalDate.of(2024, 3, 5), null, null);
        rental1.setId(1L);
        rental2.setId(2L);

        List<Rental> rentals = List.of(rental1, rental2);

        given(rentalService.findRentalsByUserId(1L)).willReturn(rentals);

        //when
        mockMvc.perform(get("/api/rentals/my"))
                //then
                .andExpect(status().isOk())
                .andExpectAll((jsonPath("$").isArray()),
                        jsonPath("$", Matchers.hasSize(2)),
                        jsonPath("[0].id").value(1),
                        jsonPath("[0].car.brand").value("Toyota"),
                        jsonPath("[0].car.model").value("Camry"),
                        jsonPath("[0].car.year").value(2020),
                        jsonPath("[0].rentalStartDate").value("2024-03-01"),

                        jsonPath("[1].car.brand").value("Honda"),
                        jsonPath("[1].car.model").value("Civic"),
                        jsonPath("[1].car.year").value(2019),
                        jsonPath("[1].rentalStartDate").value("2024-03-05"));

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
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", 1L);
        claims.put("scope", "SCOPE_ROLE_USER");
        claims.put("sub", "username");

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                claims
        );
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_USER")),
                "username"
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        given(rentalService.findRentalsByUserId(any())).willReturn(List.of());

        //when
        mockMvc.perform(get("/api/rentals/my"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}
