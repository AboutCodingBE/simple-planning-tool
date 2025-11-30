package be.aboutcoding.simpleplanningtool.worker;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WorkerApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void cleanupDatabase() {
        entityManager.createQuery("DELETE FROM Worker").executeUpdate();
    }

    @Test
    void shouldPersistWorkerWhenValidRequestIsMade() throws Exception {
        // Given
        String requestBody = """
                {
                  "first_name": "John",
                  "last_name": "Doe"
                }
                """;

        // When
        String responseBody = mockMvc.perform(post("/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long workerId = Long.parseLong(responseBody);

        // Then
        entityManager.flush();
        entityManager.clear();

        Worker persistedWorker = entityManager
                .createQuery("SELECT w FROM Worker w WHERE w.id = :id", Worker.class)
                .setParameter("id", workerId)
                .getSingleResult();

        assertThat(persistedWorker.getId()).isEqualTo(workerId);
        assertThat(persistedWorker.getFirstName()).isEqualTo("John");
        assertThat(persistedWorker.getLastName()).isEqualTo("Doe");
        assertThat(persistedWorker.getDateOfCreation()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidCreateWorkerRequests")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsMissing(String requestBody) throws Exception {
        mockMvc.perform(post("/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidCreateWorkerRequests() {
        return Stream.of(
                // Missing first_name
                """
                {
                  "last_name": "Doe"
                }
                """,
                // Missing last_name
                """
                {
                  "first_name": "John"
                }
                """,
                // Missing both
                """
                {
                }
                """
        );
    }
}
