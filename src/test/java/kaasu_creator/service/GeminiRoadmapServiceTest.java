package kaasu_creator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class GeminiRoadmapServiceTest {

    private GeminiRoadmapService service;

    @BeforeEach
    void setUp() {
        // A configured key so generateRoadmap's key check passes where relevant.
        service = new GeminiRoadmapService(new ObjectMapper(),
                "test-key", "gemini-1.5-flash",
                "https://example.invalid/models", 1000, 1000);
    }

    @Test
    void parseRoadmap_extractsGeneratedText() {
        String body = """
            {"candidates":[{"content":{"parts":[{"text":"  Save 20% each month.  "}]}}]}
            """;

        assertThat(service.parseRoadmap(body)).isEqualTo("Save 20% each month.");
    }

    @Test
    void parseRoadmap_throwsWhenNoCandidates() {
        assertThatThrownBy(() -> service.parseRoadmap("{\"candidates\":[]}"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no candidates");
    }

    @Test
    void parseRoadmap_throwsWhenCandidatesMissing() {
        assertThatThrownBy(() -> service.parseRoadmap("{}"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void parseRoadmap_throwsOnBlankBody() {
        assertThatThrownBy(() -> service.parseRoadmap("   "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("empty response");
    }

    @Test
    void parseRoadmap_throwsOnMalformedJson() {
        assertThatThrownBy(() -> service.parseRoadmap("not json"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void generateRoadmap_throwsWhenApiKeyMissing() {
        GeminiRoadmapService unconfigured = new GeminiRoadmapService(new ObjectMapper(),
                "", "gemini-1.5-flash", "https://example.invalid/models", 1000, 1000);

        assertThatThrownBy(() -> unconfigured.generateRoadmap(
                new BigDecimal("3000"), new BigDecimal("2000"), null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not configured");
    }
}
