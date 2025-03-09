package lt.techin.UserControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.controller.UserController.UserControllerPublic;
import lt.techin.dto.RoleDTO;
import lt.techin.dto.UserRequestDTO;
import lt.techin.model.Role;
import lt.techin.model.User;
import lt.techin.security.SecurityConfig;
import lt.techin.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerPublic.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UserControllerPublicPOSTTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //happy path
    @Test
    void addUser_whenValidRequest_thenReturnAnd201() throws Exception {
        //given
        UserRequestDTO userRequestDTO = new UserRequestDTO("username", "password", List.of(new RoleDTO("ROLE_USER")));

        Role role = new Role("ROLE_CLIENT");
        role.setId(1L);
        User user = new User("username", "hashedPassword", List.of(role), List.of());
        user.setId(1L);

        when(userService.existsUserByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.saveUser(any())).thenReturn(user);

        //when
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value("username"))
                .andExpect(jsonPath("roles").isArray())
                .andExpect(jsonPath("roles", Matchers.hasSize(1)))
                .andExpect(jsonPath("roles[0]").exists());

        Mockito.verify(userService, times(1)).saveUser(ArgumentMatchers.any(User.class));
    }
}
