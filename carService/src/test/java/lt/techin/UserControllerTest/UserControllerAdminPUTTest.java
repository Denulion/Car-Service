package lt.techin.UserControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.UserController.UserControllerAdmin;
import lt.techin.dto.RoleDTO;
import lt.techin.dto.UserRequestDTO;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UserControllerAdminPUTTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void updateUser_whenValid_thenReturnAnd200() throws Exception {
        //given
        long userId = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO("username", "password", List.of(new RoleDTO("ROLE_USER")));

        Role role = new Role("ROLE_CLIENT");
        role.setId(1L);

        User existingUser = new User("oldUsername", "oldPassword", List.of(role), List.of());
        existingUser.setId(1L);

        when(userService.existsUserById(userId)).thenReturn(true);
        when(userService.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.saveUser(any())).thenReturn(existingUser);

        //when
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("username"))
                .andExpect(jsonPath("roles", Matchers.hasSize(1)))
                .andExpect(jsonPath("roles[0]").exists());

        Mockito.verify(userService, times(1)).saveUser(any());
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void updateUser_whenUserWithIdDoesntExist_thenReturnAnd404() throws Exception {
        //given
        long userId = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO("username", "password", List.of(new RoleDTO("ROLE_USER")));

        given(userService.existsUserById(userId)).willReturn(false);

        //when
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("This user does not exist!"));

        Mockito.verify(userService, times(0)).saveUser(any());
    }

    //unhappy path
    @Test
    void updateUser_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        long userId = 1L;
        UserRequestDTO userRequestDTO = new UserRequestDTO("username", "password", List.of(new RoleDTO("ROLE_USER")));

        //when
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(0)).saveUser(any());
    }
}
