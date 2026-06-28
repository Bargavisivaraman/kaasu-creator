package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.service.AuthService;
import kaasu_creator.service.EmailAlreadyRegisteredException;

/**
 * Web-layer tests for AuthController registration. Security filters are
 * disabled so the test focuses on the controller's redirect behavior.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void register_redirectsToLogin_onSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "New User")
                        .param("email", "new@example.com")
                        .param("password", "secret"))
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(authService).register("New User", "new@example.com", "secret");
    }

    @Test
    void register_redirectsBackToRegister_onDuplicateEmail() throws Exception {
        doThrow(new EmailAlreadyRegisteredException())
                .when(authService).register(anyString(), anyString(), anyString());

        mockMvc.perform(post("/register")
                        .param("fullName", "Dup")
                        .param("email", "taken@example.com")
                        .param("password", "secret"))
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"));
    }
}
