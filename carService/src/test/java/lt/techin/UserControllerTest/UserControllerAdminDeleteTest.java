package lt.techin.UserControllerTest;

import lt.techin.controller.UserController.UserControllerAdmin;
import lt.techin.security.SecurityConfig;
import lt.techin.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerAdmin.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class UserControllerAdminDeleteTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    //happy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void deleteUser_whenAdminDeletesMovie_thenReturnAnd204() throws Exception {
        //given
        long userId = 1L;

        given(userService.existsUserById(userId)).willReturn(true);
        Mockito.doNothing().when(userService).deleteUserById(userId);

        //when
        mockMvc.perform(delete("/api/users/{id}", userId))
                //then
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(1)).deleteUserById(userId);
    }

    //unhappy path
    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void deleteUser_whenUserDoesNotExist_thenReturnAnd404() throws Exception {
        //given
        long userId = 1L;

        given(userService.existsUserById(userId)).willReturn(false);

        //when
        mockMvc.perform(delete("/api/users/{id}", userId))
                //then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        Mockito.verify(userService, times(0)).deleteUserById(userId);
    }

    //unhappy path
    @Test
    void deleteUser_whenUnauthenticated_thenReturnAnd401() throws Exception {
        //given
        long userId = 1L;

        //when
        mockMvc.perform(delete("/api/users/{id}", userId))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
