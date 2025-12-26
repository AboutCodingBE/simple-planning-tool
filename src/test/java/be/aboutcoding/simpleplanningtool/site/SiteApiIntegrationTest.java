package be.aboutcoding.simpleplanningtool.site;

import be.aboutcoding.simpleplanningtool.worker.Worker;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
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
class SiteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void cleanupDatabase() {
        entityManager.createQuery("DELETE FROM Site").executeUpdate();
        entityManager.createQuery("DELETE FROM Customer").executeUpdate();
        entityManager.createQuery("DELETE FROM Worker").executeUpdate();
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
        assertThat(persistedSite.getStatus()).isEqualTo(SiteStatus.OPEN);
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

    @Test
    void shouldDeleteSiteWhenValidIdIsProvided() throws Exception {
        // Given - create a customer and site in the database
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setIsPrivate(false);

        Site site = new Site();
        site.setName("Test Site");
        site.setCustomer(customer);
        site.setCreationDate(java.time.Instant.now());

        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();
        Long customerId = customer.getId();

        // When - send DELETE request
        mockMvc.perform(delete("/sites/" + siteId))
                .andExpect(status().isOk());

        // Then - verify site is deleted
        entityManager.flush();
        entityManager.clear();

        Long siteCount = entityManager
                .createQuery("SELECT COUNT(s) FROM Site s WHERE s.id = :id", Long.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(siteCount).isEqualTo(0L);

        // Verify customer is also deleted
        Long customerCount = entityManager
                .createQuery("SELECT COUNT(c) FROM Customer c WHERE c.id = :id", Long.class)
                .setParameter("id", customerId)
                .getSingleResult();

        assertThat(customerCount).isEqualTo(0L);
    }

    @Test
    void shouldReturnOkWhenDeletingNonExistentSite() throws Exception {
        // Given - a site ID that doesn't exist
        Long nonExistentId = 99999L;

        // When / Then - send DELETE request
        mockMvc.perform(delete("/sites/" + nonExistentId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSiteWithCustomerAndWorkersWhenValidIdIsProvided() throws Exception {
        // Given - create customer, workers and site in the database
        Customer customer = new Customer();
        customer.setName("Acme Corporation");
        customer.setIsPrivate(false);

        Worker worker1 = new Worker("Jane", "Workhard");
        Worker worker2 = new Worker("John", "Builder");

        Site site = new Site();
        site.setName("Construction Site A");
        site.setCustomer(customer);
        site.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 10));
        site.setExecutionDate(LocalDate.of(2026, Month.JANUARY, 15));
        site.setCreationDate(Instant.parse("2025-12-01T10:30:00.000Z"));
        site.setDurationInDays(7);
        site.setTransport("Truck and Crane");
        site.setWorkers(List.of(worker1, worker2));
        site.setStatus(SiteStatus.OPEN);

        entityManager.persist(worker1);
        entityManager.persist(worker2);
        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();

        // When - send GET request
        mockMvc.perform(get("/sites/" + siteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(siteId))
                .andExpect(jsonPath("$.name").value("Construction Site A"))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.customer.customer_name").value("Acme Corporation"))
                .andExpect(jsonPath("$.customer.is_private_customer").value(false))
                .andExpect(jsonPath("$.desired_date").value("2026-01-10"))
                .andExpect(jsonPath("$.planned_date").value("2026-01-15"))
                .andExpect(jsonPath("$.creation_date").value("2025-12-01T10:30:00Z"))
                .andExpect(jsonPath("$.duration_in_days").value(7))
                .andExpect(jsonPath("$.workers").isArray())
                .andExpect(jsonPath("$.workers.length()").value(2))
                .andExpect(jsonPath("$.workers[0].id").value(worker1.getId()))
                .andExpect(jsonPath("$.workers[0].first_name").value("Jane"))
                .andExpect(jsonPath("$.workers[0].last_name").value("Workhard"))
                .andExpect(jsonPath("$.workers[1].id").value(worker2.getId()))
                .andExpect(jsonPath("$.workers[1].first_name").value("John"))
                .andExpect(jsonPath("$.workers[1].last_name").value("Builder"));
    }

    @Test
    void shouldReturnNotFoundWhenSiteDoesNotExist() throws Exception {
        // Given - a site ID that doesn't exist
        Long nonExistentId = 99999L;

        // When / Then - send GET request and expect 404
        mockMvc.perform(get("/sites/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOnlyOpenSitesWhenFetchingAllOpenSites() throws Exception {
        // Given - create two sites: one with status OPEN and one with status DONE
        Customer customer1 = new Customer();
        customer1.setName("Open Site Customer");
        customer1.setIsPrivate(false);

        Customer customer2 = new Customer();
        customer2.setName("Done Site Customer");
        customer2.setIsPrivate(true);

        Site openSite = new Site();
        openSite.setName("Open Construction Site");
        openSite.setCustomer(customer1);
        openSite.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 10));
        openSite.setExecutionDate(LocalDate.of(2026, Month.JANUARY, 10));
        openSite.setCreationDate(Instant.now());
        openSite.setDurationInDays(4);
        openSite.setStatus(SiteStatus.OPEN);

        Site doneSite = new Site();
        doneSite.setName("Completed Construction Site");
        doneSite.setCustomer(customer2);
        doneSite.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 15));
        doneSite.setExecutionDate(LocalDate.of(2026, Month.JANUARY, 16));
        doneSite.setCreationDate(Instant.now());
        doneSite.setDurationInDays(13);
        doneSite.setStatus(SiteStatus.DONE);

        entityManager.persist(openSite);
        entityManager.persist(doneSite);
        entityManager.flush();
        entityManager.clear();

        // When - send GET request to fetch all open sites
        mockMvc.perform(get("/sites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(openSite.getId()))
                .andExpect(jsonPath("$[0].name").value("Open Construction Site"))
                .andExpect(jsonPath("$[0].customer_name").value("Open Site Customer"))
                .andExpect(jsonPath("$[0].duration_in_days").value(4))
                .andExpect(jsonPath("$[0].desired_date").value("2026-01-10"))
                .andExpect(jsonPath("$[0].planned_date").value("2026-01-10"));
    }

    @Test
    void shouldReturnEmptyListWhenNoOpenSitesExist() throws Exception {
        // Given - create a site with status DONE (no open sites)
        Customer customer = new Customer();
        customer.setName("Done Site Customer");
        customer.setIsPrivate(false);

        Site doneSite = new Site();
        doneSite.setName("Completed Construction Site");
        doneSite.setCustomer(customer);
        doneSite.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 15));
        doneSite.setExecutionDate(LocalDate.of(2026, Month.JANUARY, 16));
        doneSite.setCreationDate(Instant.now());
        doneSite.setDurationInDays(10);
        doneSite.setStatus(SiteStatus.DONE);

        entityManager.persist(doneSite);
        entityManager.flush();
        entityManager.clear();

        // When - send GET request to fetch all open sites
        mockMvc.perform(get("/sites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldUpdateSiteWhenValidUpdateRequestIsMade() throws Exception {
        // Given - create an existing site with a customer
        Customer originalCustomer = new Customer();
        originalCustomer.setName("Original Customer");
        originalCustomer.setIsPrivate(false);

        Site originalSite = new Site();
        originalSite.setName("Original Site Name");
        originalSite.setCustomer(originalCustomer);
        originalSite.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 10));
        originalSite.setDurationInDays(5);
        originalSite.setTransport("Van");
        originalSite.setCreationDate(Instant.now());
        originalSite.setStatus(SiteStatus.OPEN);

        entityManager.persist(originalSite);
        entityManager.flush();
        entityManager.clear();

        Long siteId = originalSite.getId();

        // When - send PUT request with updated data
        String updateRequestBody = """
                {
                  "name": "Updated Site Name",
                  "customer_name": "Updated Customer",
                  "is_private_customer": true,
                  "desired_date": "2026-02-15",
                  "duration_in_days": 10,
                  "transport": "Truck and Crane"
                }
                """;

        mockMvc.perform(put("/sites/" + siteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isNoContent());

        // Then - verify the site is updated in the database
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getName()).isEqualTo("Updated Site Name");
        assertThat(updatedSite.getDesiredDate()).isEqualTo(LocalDate.of(2026, Month.FEBRUARY, 15));
        assertThat(updatedSite.getDurationInDays()).isEqualTo(10);
        assertThat(updatedSite.getTransport()).isEqualTo("Truck and Crane");

        // Verify customer is also updated
        assertThat(updatedSite.getCustomer()).isNotNull();
        assertThat(updatedSite.getCustomer().getName()).isEqualTo("Updated Customer");
        assertThat(updatedSite.getCustomer().getIsPrivate()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateSiteRequests")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid(String requestBody) throws Exception {
        // Given - create an existing site with a customer
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setIsPrivate(false);

        Site site = new Site();
        site.setName("Test Site");
        site.setCustomer(customer);
        site.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 10));
        site.setDurationInDays(5);
        site.setTransport("Van");
        site.setCreationDate(Instant.now());
        site.setStatus(SiteStatus.OPEN);

        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();

        // When / Then - send PUT request with invalid data
        mockMvc.perform(put("/sites/" + siteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentSite() throws Exception {
        // Given - a site ID that doesn't exist
        Long nonExistentId = 99999L;

        String updateRequestBody = """
                {
                  "name": "Updated Site Name",
                  "customer_name": "Updated Customer",
                  "is_private_customer": true,
                  "desired_date": "2026-02-15",
                  "duration_in_days": 10,
                  "transport": "Truck and Crane"
                }
                """;

        // When / Then - send PUT request and expect 404
        mockMvc.perform(put("/sites/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddCustomerToSiteWhenUpdatingSiteWithoutCustomer() throws Exception {
        // Given - create a site without a customer
        Site siteWithoutCustomer = new Site();
        siteWithoutCustomer.setName("Site Without Customer");
        siteWithoutCustomer.setCustomer(null);
        siteWithoutCustomer.setDesiredDate(LocalDate.of(2026, Month.JANUARY, 10));
        siteWithoutCustomer.setDurationInDays(5);
        siteWithoutCustomer.setTransport("Van");
        siteWithoutCustomer.setCreationDate(Instant.now());
        siteWithoutCustomer.setStatus(SiteStatus.OPEN);

        entityManager.persist(siteWithoutCustomer);
        entityManager.flush();
        entityManager.clear();

        Long siteId = siteWithoutCustomer.getId();

        // When - send PUT request with customer data
        String updateRequestBody = """
                {
                  "name": "Updated Site Name",
                  "customer_name": "New Customer",
                  "is_private_customer": true,
                  "desired_date": "2026-02-15",
                  "duration_in_days": 10,
                  "transport": "Truck and Crane"
                }
                """;

        mockMvc.perform(put("/sites/" + siteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestBody))
                .andExpect(status().isNoContent());

        // Then - verify the site now has a customer
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getCustomer()).isNotNull();
        assertThat(updatedSite.getCustomer().getName()).isEqualTo("New Customer");
        assertThat(updatedSite.getCustomer().getIsPrivate()).isTrue();

        // Verify customer is persisted in the database
        Customer persistedCustomer = entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.id = :id", Customer.class)
                .setParameter("id", updatedSite.getCustomer().getId())
                .getSingleResult();

        assertThat(persistedCustomer.getName()).isEqualTo("New Customer");
        assertThat(persistedCustomer.getIsPrivate()).isTrue();
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

    private static Stream<String> invalidUpdateSiteRequests() {
        return Stream.of(
                // Missing name
                """
                {
                  "customer_name": "Updated Customer",
                  "is_private_customer": true,
                  "duration_in_days": 10
                }
                """,
                // Missing customer_name
                """
                {
                  "name": "Updated Site",
                  "is_private_customer": true,
                  "duration_in_days": 10
                }
                """,
                // Missing is_private_customer
                """
                {
                  "name": "Updated Site",
                  "customer_name": "Updated Customer",
                  "duration_in_days": 10
                }
                """,
                // Missing duration_in_days
                """
                {
                  "name": "Updated Site",
                  "customer_name": "Updated Customer",
                  "is_private_customer": true
                }
                """,
                // Empty name
                """
                {
                  "name": "",
                  "customer_name": "Updated Customer",
                  "is_private_customer": true,
                  "duration_in_days": 10
                }
                """,
                // Empty customer_name
                """
                {
                  "name": "Updated Site",
                  "customer_name": "",
                  "is_private_customer": true,
                  "duration_in_days": 10
                }
                """,
                // Null is_private_customer
                """
                {
                  "name": "Updated Site",
                  "customer_name": "Updated Customer",
                  "is_private_customer": null,
                  "duration_in_days": 10
                }
                """,
                // Null duration_in_days
                """
                {
                  "name": "Updated Site",
                  "customer_name": "Updated Customer",
                  "is_private_customer": true,
                  "duration_in_days": null
                }
                """
        );
    }
}
