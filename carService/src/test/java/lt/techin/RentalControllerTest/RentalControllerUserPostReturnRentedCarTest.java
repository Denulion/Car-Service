package lt.techin.RentalControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.RentalController.RentalControllerUser;
import lt.techin.model.*;
import lt.techin.security.SecurityConfig;
import lt.techin.service.CarService;
import lt.techin.service.RentalService;
import lt.techin.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RentalControllerUser.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class RentalControllerUserPostReturnRentedCarTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RentalService rentalService;
    @MockitoBean
    private CarService carService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    public void setupAuth() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", 1L);
        claims.put("scope", "SCOPE_RULE_USER");
        claims.put("sub", "username");

        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600L),
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
    }

    //happy path
    @Test
    void returnRentedCar_whenValid_thenReturnAnd201() throws Exception {
        //given
        setupAuth();

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Car car = new Car("Toyota", "Camry", 2020, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        Rental rental = new Rental(user, car, LocalDate.of(2025, 3, 21), null, null);
        rental.setId(1L);

        when(rentalService.findRentalsByCarId(1L)).thenReturn((List.of(rental)));
        doAnswer(invocationOnMock -> {
            Rental r = invocationOnMock.getArgument(0);
            r.setRentalEnd(LocalDate.now());
            long totalDays = Math.max(1, r.getTotalDays());
            r.setPrice(BigDecimal.valueOf(totalDays).multiply(r.getCar().getDailyRentPrice()));
            r.getCar().setStatus(CarStatus.valueOf("AVAILABLE"));
            return null;
        }).when(rentalService).calculatePriceAndReturnCar(rental);

        //when
        mockMvc.perform(post("/api/rentals/return/1")
                        .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpectAll((jsonPath("$.id").value(1L)),
                        (jsonPath("$.user.username").value("username")),
                        (jsonPath("$.car.id").value(1L)),
                        (jsonPath("$.car.brand").value("Toyota")),
                        (jsonPath("$.car.model").value("Camry")),
                        (jsonPath("$.car.status").value("AVAILABLE")),
                        (jsonPath("$.rentalStartDate").value("2025-03-21")),
                        (jsonPath("$.rentalEndDate").value(LocalDate.now().toString())),
                        (jsonPath("$.totalPrice").value(100.00)));

        Mockito.verify(rentalService, times(1)).findRentalsByCarId(1L);
        Mockito.verify(rentalService, times(1)).calculatePriceAndReturnCar(rental);
    }
}
