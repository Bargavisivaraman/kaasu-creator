package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.model.User;
import kaasu_creator.service.CurrentUserService;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void showProfile_usesFullNameWhenPresent() throws Exception {
        when(currentUserService.requireUser(any()))
                .thenReturn(new User(1L, "Ada Lovelace", "ada@example.com", "hash", null));

        mockMvc.perform(get("/profile"))
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("userName", "Ada Lovelace"))
                .andExpect(model().attribute("userEmail", "ada@example.com"));
    }

    @Test
    void showProfile_fallsBackToEmailWhenNameBlank() throws Exception {
        when(currentUserService.requireUser(any()))
                .thenReturn(new User(1L, "   ", "ada@example.com", "hash", null));

        mockMvc.perform(get("/profile"))
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("userName", "ada@example.com"));
    }
}
