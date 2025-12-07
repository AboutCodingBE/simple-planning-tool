package be.aboutcoding.simpleplanningtool.site;

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

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SiteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void cleanupDatabase() {
        entityManager.createQuery("DELETE FROM Site").executeUpdate();
        entityManager.createQuery("DELETE FROM Customer").executeUpdate();
    }

    @Test
    void shouldPersistSiteAndCustomerWhenValidRequestIsMade() throws Exception {
        // Given
        String requestBody = """
                {
                  "name": "Construction Site A",
                  "customer_name": "Acme Corporation",
                  "is_private_customer": true,
                  "desired_date": "2026-01-10",
                  "duration_in_days": 7,
                  "transport": "Truck and Crane"
                }
                """;

        // When
        String responseBody = mockMvc.perform(post("/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long siteId = Long.parseLong(responseBody);

        // Then
        entityManager.flush();
        entityManager.clear();

        Site persistedSite = entityManager
                .createQuery("SELECT s FROM Site s WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(persistedSite.getId()).isEqualTo(siteId);
        assertThat(persistedSite.getName()).isEqualTo("Construction Site A");
        assertThat(persistedSite.getDesiredDate()).isEqualTo(LocalDate.of(2026, Month.JANUARY, 10));
        assertThat(persistedSite.getDurationInDays()).isEqualTo(7);
        assertThat(persistedSite.getTransport()).isEqualTo("Truck and Crane");
        assertThat(persistedSite.getCreationDate()).isNotNull();

        // Verify customer is also persisted
        assertThat(persistedSite.getCustomer()).isNotNull();
        assertThat(persistedSite.getCustomer().getName()).isEqualTo("Acme Corporation");
        assertThat(persistedSite.getCustomer().getIsPrivate()).isTrue();

        // Verify customer exists in database
        Customer persistedCustomer = entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.id = :id", Customer.class)
                .setParameter("id", persistedSite.getCustomer().getId())
                .getSingleResult();

        assertThat(persistedCustomer.getName()).isEqualTo("Acme Corporation");
        assertThat(persistedCustomer.getIsPrivate()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidSiteRequests")
    void shouldReturnBadRequestWhenNameOrCustomerNameIsMissing(String requestBody) throws Exception {
        mockMvc.perform(post("/sites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidSiteRequests() {
        return Stream.of(
                // Missing name
                """
                {
                  "customer_name": "Acme Corporation"
                }
                """,
                // Missing customer_name
                """
                {
                  "name": "Construction Site A"
                }
                """,
                // Missing both
                """
                {
                }
                """,
                // Empty name
                """
                {
                  "name": "",
                  "customer_name": "Acme Corporation"
                }
                """,
                // Empty customer_name
                """
                {
                  "name": "Construction Site A",
                  "customer_name": ""
                }
                """
        );
    }
}
