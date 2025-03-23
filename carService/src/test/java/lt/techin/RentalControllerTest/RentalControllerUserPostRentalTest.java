package lt.techin.RentalControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.RentalController.RentalControllerUser;
import lt.techin.dto.RentalRequestDTO;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RentalControllerUser.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class RentalControllerUserPostRentalTest {

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
    }

    //happy path
    @Test
    void postRental_whenValid_thenReturnAnd201() throws Exception {
        //given
        setupAuth();

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Car car = new Car("Toyota", "Camry", 2020, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car.setId(1L);

        RentalRequestDTO rentalRequestDTO = new RentalRequestDTO(1L, LocalDate.now().plusDays(1));

        when(rentalService.findAllRentalsByUserId(1L)).thenReturn(List.of());
        when(carService.findCarById(1L)).thenReturn(Optional.of(car));
        when(userService.findUserById(1L)).thenReturn(Optional.of(user));
        when(rentalService.save(any(Rental.class))).thenAnswer(invocationOnMock -> {
            Rental r = invocationOnMock.getArgument(0);
            r.getCar().setStatus(CarStatus.RENTED);
            r.setId(1L);
            return r;
        });

        //when
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequestDTO)))
                //then
                .andExpect(status().isCreated())
                .andExpectAll((jsonPath("id").value(1L)),
                        (jsonPath("car.id").value(1L)),
                        (jsonPath("rentalStartDate").value(LocalDate.now().plusDays(1).toString())),
                        (jsonPath("car.status").value("RENTED")));

        Mockito.verify(rentalService, times(1)).save(any());
    }

    //unhappy path
    @Test
    void postRental_whenUserHas2Rentals_thenReturnAnd400() throws Exception {
        //given
        setupAuth();

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car1.setId(1L);
        Car car2 = new Car("Audi", "Model 1", 2021, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car2.setId(2L);
        Car car3 = new Car("Audi", "Model 2", 2021, CarStatus.AVAILABLE, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car3.setId(3L);

        Rental rental1 = new Rental(user, car1, LocalDate.of(2025, 3, 21), null, null);
        rental1.setId(1L);
        Rental rental2 = new Rental(user, car2, LocalDate.of(2025, 3, 20), null, null);
        rental2.setId(2L);

        RentalRequestDTO rentalRequestDTO = new RentalRequestDTO(3L, LocalDate.now().plusDays(1));

        when(rentalService.findAllRentalsByUserId(1L)).thenReturn(List.of(rental1, rental2));

        //when
        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rentalRequestDTO)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You already have 2 cars rented!"));

        Mockito.verify(rentalService, times(1)).findAllRentalsByUserId(1L);
    }

    //unhappy path
    @Test
    void postRental_whenCarNotFound_thenReturnAnd404() throws Exception {
        //given
        setupAuth();

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user = new User("username", "password", List.of(role), List.of());
        user.setId(1L);

        RentalRequestDTO rentalRequestDTO = new RentalRequestDTO(1L, LocalDate.now().plusDays(1));

        when(rentalService.findAllRentalsByUserId(1L)).thenReturn(List.of());
        when(carService.findCarById(1L)).thenReturn(Optional.empty());

        //when
        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rentalRequestDTO)))
                //then
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found"));

        Mockito.verify(rentalService, times(1)).findAllRentalsByUserId(1L);
        Mockito.verify(carService, times(1)).findCarById(1L);
        Mockito.verify(userService, times(0)).findUserById(1L);
    }

    //unhappy path
    @Test
    void postRental_whenCarIsRented_thenReturnAnd400() throws Exception {
        //given
        setupAuth();

        Role role = new Role("ROLE_USER");
        role.setId(1L);

        User user1 = new User("username", "password", List.of(role), List.of());
        user1.setId(1L);

        User user2 = new User("username1", "password1", List.of(role), List.of());
        user2.setId(2L);

        Car car1 = new Car("Toyota", "Camry", 2020, CarStatus.RENTED, new ArrayList<>(), BigDecimal.valueOf(50.00));
        car1.setId(1L);

        RentalRequestDTO rentalRequestDTO = new RentalRequestDTO(1L, LocalDate.now().plusDays(1));

        when(rentalService.findAllRentalsByUserId(1L)).thenReturn(List.of());
        when(carService.findCarById(1L)).thenReturn(Optional.of(car1));
        when(userService.findUserById(1L)).thenReturn(Optional.of(user1));

        //when
        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rentalRequestDTO)))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("This car is already rented!"));

        Mockito.verify(rentalService, times(1)).findAllRentalsByUserId(1L);
        Mockito.verify(carService, times(1)).findCarById(1L);
        Mockito.verify(userService, times(1)).findUserById(1L);
        Mockito.verify(rentalService, times(0)).save(any());
    }

    //unhappy path
    @Test
    void postRental_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given no auth

        //when
        mockMvc.perform(post("/api/rentals"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(rentalService, never()).findAllRentalsByUserId(1L);
    }
}
