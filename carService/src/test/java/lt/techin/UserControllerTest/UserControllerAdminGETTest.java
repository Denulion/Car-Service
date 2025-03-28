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

import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = UserControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UserControllerAdminGETTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getUsers_whenAuthenticatedAsAdmin_thenReturnAnd200() throws Exception {
        //given
        Role role = new Role("USER");
        role.setId(1L);

        User user1 = new User("username1", "password1", List.of(role), List.of());
        User user2 = new User("username2", "password2", List.of(role), List.of());

        List<User> users = List.of(user1, user2);

        given(userService.findAllUsers()).willReturn(users);

        //when
        mockMvc.perform(get("/api/users"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].username").value("username1"))
                .andExpect(jsonPath("[0].roles").isArray())
                .andExpect(jsonPath("[0].roles", Matchers.hasSize(1)))
                .andExpect(jsonPath("[0].roles.[0].id").value(1L))

                .andExpect(jsonPath("[1].username").value("username2"))
                .andExpect(jsonPath("[1].roles").isArray())
                .andExpect(jsonPath("[1].roles", Matchers.hasSize(1)))
                .andExpect(jsonPath("[1].roles.[0].id").value(1L));

        Mockito.verify(userService, times(1)).findAllUsers();
    }

    //unhappy path
    @Test
    void getUsers_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        Role role = new Role("USER");

        User user1 = new User("username1", "password1", List.of(role), List.of());
        User user2 = new User("username2", "password2", List.of(role), List.of());

        List<User> users = List.of(user1, user2);

        given(userService.findAllUsers()).willReturn(users);

        //when
        mockMvc.perform(get("/api/users"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(0)).findAllUsers();
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void getUsers_whenFindAllEmpty_thenReturnEmptyListAnd200() throws Exception{
        //given
        given(userService.findAllUsers()).willReturn(List.of());

        //when
        mockMvc.perform(get("/api/users"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }
}
