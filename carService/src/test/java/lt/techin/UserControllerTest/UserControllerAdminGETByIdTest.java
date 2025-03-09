package lt.techin.UserControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.UserController.UserControllerAdmin;
import lt.techin.model.Role;
import lt.techin.model.User;
import lt.techin.security.SecurityConfig;
import lt.techin.service.UserService;
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

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = UserControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UserControllerAdminGETByIdTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getUser_whenValid_thenReturnAnd200() throws Exception {
        //given
        long userId = 1L;

        User user1 = new User("username1", "password1", List.of(new Role("USER")), List.of());

        given(userService.findUserById(userId)).willReturn(Optional.of(user1));

        //when
        mockMvc.perform(get("/api/users/{id}", userId))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("username1"))
                .andExpect(jsonPath("roles").isArray())
                .andExpect(jsonPath("roles", Matchers.hasSize(1)))
                .andExpect(jsonPath("roles.[0].name").value("USER"));

        Mockito.verify(userService, times(1)).findUserById(userId);
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getUser_whenFindNone_thenReturnAnd404() throws Exception {
        //given
        long userId = 1L;

        given(userService.findUserById(userId)).willReturn(Optional.empty());

        //when
        mockMvc.perform(get("/api/users/{id}", userId))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(1)).findUserById(userId);
    }

    //unhappy path
    @Test
    void getUser_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        long userId = 1L;

        User user1 = new User("username1", "password1", List.of(new Role("USER")), List.of());

        given(userService.findUserById(userId)).willReturn(Optional.empty());

        //when
        mockMvc.perform(get("/api/users/{id}", userId))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(0)).findUserById(userId);
    }
}
