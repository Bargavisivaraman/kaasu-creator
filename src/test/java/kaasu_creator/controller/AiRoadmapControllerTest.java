package kaasu_creator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kaasu_creator.service.GeminiRoadmapService;

@WebMvcTest(AiRoadmapController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiRoadmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeminiRoadmapService geminiRoadmapService;

    @Test
    void generateRoadmap_rejectsNonPositiveIncome_withoutCallingGemini() throws Exception {
        mockMvc.perform(post("/generate-roadmap")
                        .param("monthlyIncome", "0")
                        .param("monthlyExpenses", "500"))
                .andExpect(view().name("ai-roadmap"))
                .andExpect(model().attributeExists("error"));

        verify(geminiRoadmapService, never()).generateRoadmap(any(), any(), any(), any());
    }

    @Test
    void generateRoadmap_withValidInput_invokesServiceAndExposesResult() throws Exception {
        when(geminiRoadmapService.generateRoadmap(any(), any(), any(), any()))
                .thenReturn("Save 20% each month.");

        mockMvc.perform(post("/generate-roadmap")
                        .param("monthlyIncome", "3000")
                        .param("monthlyExpenses", "2000"))
                .andExpect(view().name("ai-roadmap"))
                .andExpect(model().attribute("generated", true))
                .andExpect(model().attribute("roadmap", "Save 20% each month."));

        verify(geminiRoadmapService).generateRoadmap(
                new BigDecimal("3000"), new BigDecimal("2000"), null, null);
    }
}
