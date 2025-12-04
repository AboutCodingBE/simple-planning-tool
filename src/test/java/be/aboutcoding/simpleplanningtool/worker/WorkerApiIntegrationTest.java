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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @MethodSource("invalidWorkerRequests")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsMissing(String requestBody) throws Exception {
        mockMvc.perform(post("/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteWorkerWhenValidIdIsProvided() throws Exception {
        // Given - create a worker in the database
        Worker worker = new Worker("Jane", "Smith");
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();

        // When - send DELETE request
        mockMvc.perform(delete("/workers/" + workerId))
                .andExpect(status().isOk());

        // Then - verify worker is deleted
        entityManager.flush();
        entityManager.clear();

        Long count = entityManager
                .createQuery("SELECT COUNT(w) FROM Worker w WHERE w.id = :id", Long.class)
                .setParameter("id", workerId)
                .getSingleResult();

        assertThat(count).isEqualTo(0L);
    }

    @Test
    void shouldReturnOkWhenDeletingNonExistentWorker() throws Exception {
        // Given - a worker ID that doesn't exist
        Long nonExistentId = 99999L;

        // When / Then - send DELETE request
        mockMvc.perform(delete("/workers/" + nonExistentId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnWorkerWhenValidIdIsProvided() throws Exception {
        // Given - create a worker in the database
        Worker worker = new Worker("Alice", "Johnson");
        worker.setDateOfCreation(java.sql.Timestamp.from(java.time.Instant.now()));
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();

        // When / Then - send GET request
        mockMvc.perform(get("/workers/" + workerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workerId))
                .andExpect(jsonPath("$.first_name").value("Alice"))
                .andExpect(jsonPath("$.last_name").value("Johnson"))
                .andExpect(jsonPath("$.date_of_creation").exists());
    }

    @Test
    void shouldReturnNotFoundWhenWorkerDoesNotExist() throws Exception {
        // Given - a worker ID that doesn't exist
        Long nonExistentId = 99999L;

        // When / Then - send GET request
        mockMvc.perform(get("/workers/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateWorkerWhenValidRequestIsMade() throws Exception {
        // Given - create a worker in the database
        Worker worker = new Worker("Bob", "Brown");
        worker.setDateOfCreation(java.sql.Timestamp.from(java.time.Instant.now()));
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();

        String updateRequestBody = """
                {
                  "first_name": "Robert",
                  "last_name": "Browning"
                }
                """;

        // When
        String responseBody = mockMvc.perform(put("/workers/" + workerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long returnedId = Long.parseLong(responseBody);

        // Then
        entityManager.flush();
        entityManager.clear();

        Worker updatedWorker = entityManager
                .createQuery("SELECT w FROM Worker w WHERE w.id = :id", Worker.class)
                .setParameter("id", workerId)
                .getSingleResult();

        assertThat(returnedId).isEqualTo(workerId);
        assertThat(updatedWorker.getId()).isEqualTo(workerId);
        assertThat(updatedWorker.getFirstName()).isEqualTo("Robert");
        assertThat(updatedWorker.getLastName()).isEqualTo("Browning");
        assertThat(updatedWorker.getDateOfCreation()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidWorkerRequests")
    void shouldReturnBadRequestWhenUpdateRequestHasMissingFields(String requestBody) throws Exception {
        // Given - create a worker in the database
        Worker worker = new Worker("Charlie", "Davis");
        worker.setDateOfCreation(java.sql.Timestamp.from(java.time.Instant.now()));
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();

        // When / Then
        mockMvc.perform(put("/workers/" + workerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentWorker() throws Exception {
        // Given - a worker ID that doesn't exist
        Long nonExistentId = 99999L;

        String updateRequestBody = """
                {
                  "first_name": "Updated",
                  "last_name": "Name"
                }
                """;

        // When / Then
        mockMvc.perform(put("/workers/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isNotFound());
    }

    private static Stream<String> invalidWorkerRequests() {
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
                """,
                """
                {
                  "first_name": ""
                }
                """
        );
    }
}
