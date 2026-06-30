package kaasu_creator.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end security checks for CSRF protection and the GET-based logout.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CsrfSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPage_rendersCsrfToken() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("_csrf")));
    }

    @Test
    void postWithoutCsrfToken_isForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "No Token")
                        .param("email", "notoken@example.com")
                        .param("password", "secret"))
                .andExpect(status().isForbidden());
    }

    @Test
    void postWithCsrfToken_isAccepted() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "With Token")
                        .param("email", "withtoken@example.com")
                        .param("password", "secret")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void logout_viaGet_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/logout").with(user("someone@example.com")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
