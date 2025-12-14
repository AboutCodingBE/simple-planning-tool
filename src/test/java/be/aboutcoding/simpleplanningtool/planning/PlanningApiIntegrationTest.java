package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.Customer;
import be.aboutcoding.simpleplanningtool.site.Site;
import jakarta.persistence.EntityManager;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PlanningApiIntegrationTest {

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
    void shouldUpdateExecutionDateWhenValidDateAndSiteIdAreProvided() throws Exception {
        // Given - create a customer and site in the database
        Site site = createAndPersistSite(null);
        Long siteId = site.getId();
        LocalDate executionDate = LocalDate.of(2026, 6, 15);

        // When - send PATCH request to plan the site
        mockMvc.perform(patch("/planning/sites/" + siteId)
                        .queryParam("date", executionDate.toString()))
                .andExpect(status().isNoContent());

        // Then - verify the execution date was updated
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getExecutionDate()).isEqualTo(executionDate);
    }

    @Test
    void shouldReturnBadRequestWhenDateIsInThePast() throws Exception {
        // Given - create a customer and site in the database
        Site site = createAndPersistSite(null);
        Long siteId = site.getId();
        LocalDate pastDate = LocalDate.of(2020, 1, 1);

        // When / Then - send PATCH request with a date in the past
        mockMvc.perform(patch("/planning/sites/" + siteId)
                        .queryParam("date", pastDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateExecutionDateWhenSiteAlreadyHasExecutionDate() throws Exception {
        // Given - create a site with an existing execution date
        Site site = createAndPersistSite(LocalDate.of(2026, 3, 10));

        Long siteId = site.getId();
        LocalDate newExecutionDate = LocalDate.of(2026, 7, 20);

        // When - send PATCH request with a new execution date
        mockMvc.perform(patch("/planning/sites/" + siteId)
                        .queryParam("date", newExecutionDate.toString()))
                .andExpect(status().isNoContent());

        // Then - verify the execution date was updated to the new value
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getExecutionDate()).isEqualTo(newExecutionDate);
    }

    @ParameterizedTest
    @MethodSource("invalidDateValues")
    void shouldReturnBadRequestWhenDateIsNullOrInvalid(String dateValue) throws Exception {
        // Given - create a customer and site in the database
        Site site = createAndPersistSite(null);
        Long siteId = site.getId();

        // When / Then - send PATCH request with invalid or null date
        if (dateValue == null) {
            mockMvc.perform(patch("/planning/sites/" + siteId))
                    .andExpect(status().isBadRequest());
        } else {
            mockMvc.perform(patch("/planning/sites/" + siteId)
                            .queryParam("date", dateValue))
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidDateValues() {
        return Stream.of(
                null,                       // Missing date parameter
                "",                         // Empty date
                "not-a-date",              // Invalid format
                "2026-13-01",              // Invalid month
                "2026-02-30",              // Invalid day
                "01-01-2026"               // Wrong format (should be ISO: yyyy-MM-dd)
        );
    }

    private Site createAndPersistSite(LocalDate executionDate) {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setIsPrivate(false);

        Site site = new Site();
        site.setName("Test Site");
        site.setCustomer(customer);
        site.setCreationDate(Instant.now());
        site.setDurationInDays(5);

        if (executionDate != null) {
            site.setExecutionDate(executionDate);
        }

        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        return site;
    }
}
