package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.Customer;
import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteStatus;
import be.aboutcoding.simpleplanningtool.worker.Worker;
import jakarta.persistence.EntityManager;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        entityManager.createQuery("DELETE FROM Worker").executeUpdate();
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

    @Test
    void shouldLinkWorkerToSiteWhenValidSiteIdAndWorkerIdAreProvided() throws Exception {
        // Given - create a site with an execution date and a worker
        Site site = createAndPersistSite(LocalDate.of(2026, 6, 15));

        Worker worker = new Worker("John", "Doe");
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();
        Long workerId = worker.getId();

        // When - send PATCH request to link the worker to the site
        mockMvc.perform(patch("/planning/sites/" + siteId + "/workers")
                        .queryParam("workerId", workerId.toString()))
                .andExpect(status().isNoContent());

        // Then - verify the worker is linked to the site
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s LEFT JOIN FETCH s.workers WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getWorkers()).isNotNull();
        assertThat(updatedSite.getWorkers()).hasSize(1);
        assertThat(updatedSite.getWorkers().get(0).getId()).isEqualTo(workerId);
        assertThat(updatedSite.getWorkers().get(0).getFirstName()).isEqualTo("John");
        assertThat(updatedSite.getWorkers().get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldReturnNotFoundWhenLinkingWorkerToNonExistentSite() throws Exception {
        // Given - create a worker but no site
        Worker worker = new Worker("Jane", "Smith");
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();
        Long nonExistentSiteId = 99999L;

        // When / Then - send PATCH request to link worker to non-existent site
        mockMvc.perform(patch("/planning/sites/" + nonExistentSiteId + "/workers")
                        .queryParam("workerId", workerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenLinkingNonExistentWorkerToSite() throws Exception {
        // Given - create a site with execution date but no worker
        Site site = createAndPersistSite(LocalDate.of(2026, 6, 15));

        Long siteId = site.getId();
        Long nonExistentWorkerId = 99999L;

        // When / Then - send PATCH request to link non-existent worker to site
        mockMvc.perform(patch("/planning/sites/" + siteId + "/workers")
                        .queryParam("workerId", nonExistentWorkerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenLinkingWorkerToSiteWithoutExecutionDate() throws Exception {
        // Given - create a site without execution date and a worker
        Site site = createAndPersistSite(null);

        Worker worker = new Worker("Bob", "Builder");
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();
        Long workerId = worker.getId();

        // When / Then - send PATCH request to link worker to site without execution date
        mockMvc.perform(patch("/planning/sites/" + siteId + "/workers")
                        .queryParam("workerId", workerId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUnlinkWorkerFromSiteWhenWorkerIsLinkedToSite() throws Exception {
        // Given - create a site with an execution date and a worker
        Site site = createAndPersistSite(LocalDate.of(2026, 6, 15));

        Worker worker = new Worker("Alice", "Engineer");
        entityManager.persist(worker);
        entityManager.flush();

        // Link the worker to the site
        site.setWorkers(List.of(worker));
        entityManager.merge(site);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();
        Long workerId = worker.getId();

        // When - send PATCH request to unlink the worker from the site
        mockMvc.perform(patch("/planning/sites/" + siteId + "/unlink")
                        .queryParam("workerId", workerId.toString()))
                .andExpect(status().isNoContent());

        // Then - verify the worker is no longer linked to the site
        entityManager.flush();
        entityManager.clear();

        Site updatedSite = entityManager
                .createQuery("SELECT s FROM Site s LEFT JOIN FETCH s.workers WHERE s.id = :id", Site.class)
                .setParameter("id", siteId)
                .getSingleResult();

        assertThat(updatedSite.getWorkers()).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenUnlinkingWorkerFromNonExistentSite() throws Exception {
        // Given - create a worker but no site
        Worker worker = new Worker("Charlie", "Developer");
        entityManager.persist(worker);
        entityManager.flush();
        entityManager.clear();

        Long workerId = worker.getId();
        Long nonExistentSiteId = 99999L;

        // When / Then - send PATCH request to unlink worker from non-existent site
        mockMvc.perform(patch("/planning/sites/" + nonExistentSiteId + "/unlink")
                        .queryParam("workerId", workerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPlanningWithWorkdaysWhenValidFromAndUntilDatesAreProvided() throws Exception {
        // Given - create a site with execution date on Dec 2, 2026
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setIsPrivate(false);

        Site site = new Site("Delhaize Waregem", 5);
        site.setCustomer(customer);
        site.setCreationDate(Instant.now());
        site.setExecutionDate(LocalDate.of(2026, 12, 2));

        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        Long siteId = site.getId();
        LocalDate fromDate = LocalDate.of(2026, 12, 1);
        LocalDate untilDate = LocalDate.of(2026, 12, 4);

        // When - send GET request to get planning
        mockMvc.perform(get("/planning")
                        .queryParam("from", fromDate.toString())
                        .queryParam("until", untilDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("2026-12-01"))
                .andExpect(jsonPath("$.until").value("2026-12-04"))
                .andExpect(jsonPath("$.weeks").isArray())
                .andExpect(jsonPath("$.weeks.length()").value(1))
                // Week 49
                .andExpect(jsonPath("$.weeks[0].week").value(49))
                // Monday - Nov 30 - no sites
                .andExpect(jsonPath("$.weeks[0].monday.date").value("2026-11-30"))
                .andExpect(jsonPath("$.weeks[0].monday.sites").isEmpty())
                // Tuesday - Dec 1 - no sites
                .andExpect(jsonPath("$.weeks[0].tuesday.date").value("2026-12-01"))
                .andExpect(jsonPath("$.weeks[0].tuesday.sites").isEmpty())
                // Wednesday - Dec 2 - has the site
                .andExpect(jsonPath("$.weeks[0].wednesday.date").value("2026-12-02"))
                .andExpect(jsonPath("$.weeks[0].wednesday.sites").isArray())
                .andExpect(jsonPath("$.weeks[0].wednesday.sites.length()").value(1))
                .andExpect(jsonPath("$.weeks[0].wednesday.sites[0].id").value(siteId))
                .andExpect(jsonPath("$.weeks[0].wednesday.sites[0].name").value("Delhaize Waregem"))
                .andExpect(jsonPath("$.weeks[0].wednesday.sites[0].duration_in_days").value(5))
                .andExpect(jsonPath("$.weeks[0].wednesday.sites[0].status").exists())
                // Thursday - Dec 3 - no sites
                .andExpect(jsonPath("$.weeks[0].thursday.date").value("2026-12-03"))
                .andExpect(jsonPath("$.weeks[0].thursday.sites").isEmpty())
                // Friday - Dec 4 - no sites
                .andExpect(jsonPath("$.weeks[0].friday.date").value("2026-12-04"))
                .andExpect(jsonPath("$.weeks[0].friday.sites").isEmpty())
                // Saturday - Dec 5 - no sites
                .andExpect(jsonPath("$.weeks[0].saturday.date").value("2026-12-05"))
                .andExpect(jsonPath("$.weeks[0].saturday.sites").isEmpty())
                // Sunday - Dec 6 - no sites
                .andExpect(jsonPath("$.weeks[0].sunday.date").value("2026-12-06"))
                .andExpect(jsonPath("$.weeks[0].sunday.sites").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenUntilDateIsBeforeFromDate() throws Exception {
        // Given - until date is before from date
        LocalDate fromDate = LocalDate.of(2026, 12, 10);
        LocalDate untilDate = LocalDate.of(2026, 12, 1);

        // When / Then - send GET request and expect bad request
        mockMvc.perform(get("/planning")
                        .queryParam("from", fromDate.toString())
                        .queryParam("until", untilDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidPlanningDateParameters")
    void shouldReturnBadRequestWhenDateParametersAreInvalid(String fromDate, String untilDate) throws Exception {
        // When / Then - send GET request with invalid date(s) and expect bad request
        mockMvc.perform(get("/planning")
                        .queryParam("from", fromDate)
                        .queryParam("until", untilDate))
                .andExpect(status().isBadRequest());
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> invalidPlanningDateParameters() {
        return Stream.of(
                // Invalid from date
                Arguments.of("not-a-date", "2026-12-10"),
                Arguments.of("2026-13-01", "2026-12-10"),
                Arguments.of("2026-02-30", "2026-12-10"),
                Arguments.of("01-12-2026", "2026-12-10"),
                // Invalid until date
                Arguments.of("2026-12-01", "not-a-date"),
                Arguments.of("2026-12-01", "2026-13-01"),
                Arguments.of("2026-12-01", "2026-02-30"),
                Arguments.of("2026-12-01", "01-12-2026"),
                // Both invalid
                Arguments.of("invalid", "also-invalid")
        );
    }

    private static Stream<String> invalidIsoDateFormats() {
        return Stream.of(
                "01-20-2026",      // Wrong format: MM-dd-yyyy
                "20/01/2026",      // Wrong format: dd/MM/yyyy
                "2026/01/20",      // Wrong format: yyyy/MM/dd (slashes instead of dashes)
                "not-a-date",      // Invalid format
                "2026-13-01",      // Invalid month
                "2026-02-30"       // Invalid day
        );
    }

    @Test
    void shouldNotReturnSitesWithoutExecutionDateOrNonOpenStatus() throws Exception {
        // Given - create three sites
        // Site 1: OPEN status with execution date (should be returned)
        Site site1 = createAndPersistSite(LocalDate.of(2026, 1, 20));
        site1.setName("Valid Site");
        site1.setDurationInDays(5);
        entityManager.merge(site1);

        // Site 2: OPEN status but NO execution date (should NOT be returned)
        Site site2 = createAndPersistSite(null);
        site2.setName("Site Without Execution Date");
        entityManager.merge(site2);

        // Site 3: Has execution date but status is DONE (should NOT be returned)
        Site site3 = createAndPersistSite(LocalDate.of(2026, 1, 20));
        site3.setName("Completed Site");
        site3.setDurationInDays(5);
        site3.setStatus(SiteStatus.DONE);
        entityManager.merge(site3);

        entityManager.flush();
        entityManager.clear();

        // Query date: 2026-01-22 (falls within site1's execution period)
        LocalDate queryDate = LocalDate.of(2026, 1, 22);

        // When - send GET request to get day overview
        mockMvc.perform(get("/planning/day")
                        .queryParam("date", queryDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-01-22"))
                .andExpect(jsonPath("$.plannedSites").isArray())
                .andExpect(jsonPath("$.plannedSites.length()").value(1))
                // Verify only site1 is returned
                .andExpect(jsonPath("$.plannedSites[0].site_id").value(site1.getId()))
                .andExpect(jsonPath("$.plannedSites[0].site_name").value("Valid Site"))
                .andExpect(jsonPath("$.plannedSites[0].site_status").value("OPEN"));
    }

    @Test
    void shouldNotReturnSiteWhenQueryDateIsAfterSiteEndDate() throws Exception {
        // Given - create a site with execution date 2026-01-20 and duration 5 days
        // End date will be 2026-01-24 (execution_date + duration - 1)
        Site site = createAndPersistSite(LocalDate.of(2026, 1, 20));
        site.setName("Construction Site");
        site.setDurationInDays(5);
        entityManager.merge(site);

        entityManager.flush();
        entityManager.clear();

        // Query date: 2026-01-25 (one day after the site's end date)
        LocalDate queryDate = LocalDate.of(2026, 1, 25);

        // When - send GET request to get day overview
        mockMvc.perform(get("/planning/day")
                        .queryParam("date", queryDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-01-25"))
                .andExpect(jsonPath("$.plannedSites").isArray())
                .andExpect(jsonPath("$.plannedSites.length()").value(0));
    }

    @ParameterizedTest
    @MethodSource("invalidIsoDateFormats")
    void shouldReturnBadRequestWhenDateIsNotInIsoFormat(String invalidDate) throws Exception {
        // When / Then - send GET request with invalid date format
        mockMvc.perform(get("/planning/day")
                        .queryParam("date", invalidDate))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOnlySiteActiveOnGivenDateWithWorker() throws Exception {
        // Given - create two sites with non-overlapping execution periods
        // Site 1: execution date 2026-01-20, duration 5 days (ends 2026-01-25)
        Site site1 = createAndPersistSite(LocalDate.of(2026, 1, 20));
        site1.setName("Downtown Office Complex");
        site1.setDurationInDays(6);
        entityManager.merge(site1);

        // Create and assign a worker to site 1
        Worker worker = new Worker("John", "Smith");
        entityManager.persist(worker);
        entityManager.flush();

        site1.setWorkers(List.of(worker));
        entityManager.merge(site1);

        // Site 2: execution date 2026-02-10, duration 3 days (ends 2026-02-13)
        Site site2 = createAndPersistSite(LocalDate.of(2026, 2, 10));
        site2.setName("Harbor Warehouse");
        site2.setDurationInDays(3);
        entityManager.merge(site2);

        entityManager.flush();
        entityManager.clear();

        // Query date: 2026-01-23 (falls within site1's period, but not site2's)
        LocalDate queryDate = LocalDate.of(2026, 1, 23);

        // When - send GET request to get day overview
        mockMvc.perform(get("/planning/day")
                        .queryParam("date", queryDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-01-23"))
                .andExpect(jsonPath("$.plannedSites").isArray())
                .andExpect(jsonPath("$.plannedSites.length()").value(1))
                // Verify site details
                .andExpect(jsonPath("$.plannedSites[0].site_id").value(site1.getId().toString()))
                .andExpect(jsonPath("$.plannedSites[0].site_name").value("Downtown Office Complex"))
                .andExpect(jsonPath("$.plannedSites[0].execution_date").value("2026-01-20"))
                .andExpect(jsonPath("$.plannedSites[0].duration_in_days").value(6))
                .andExpect(jsonPath("$.plannedSites[0].end_date").value("2026-01-25"))
                .andExpect(jsonPath("$.plannedSites[0].days_remaining").value(3))
                .andExpect(jsonPath("$.plannedSites[0].site_status").value("OPEN"))
                // Verify worker details
                .andExpect(jsonPath("$.plannedSites[0].workers").isArray())
                .andExpect(jsonPath("$.plannedSites[0].workers.length()").value(1))
                .andExpect(jsonPath("$.plannedSites[0].workers[0].worker_id").value(worker.getId().toString()))
                .andExpect(jsonPath("$.plannedSites[0].workers[0].worker_firstname").value("John"))
                .andExpect(jsonPath("$.plannedSites[0].workers[0].worker_lastname").value("Smith"));
    }

    @Test
    void shouldReturnIdleWorkersWhenSomeWorkersAreNotAssignedToAnySite() throws Exception {
        // Given - create three workers
        Worker assignedWorker = new Worker("John", "Assigned");
        Worker idleWorker1 = new Worker("Jane", "Idle");
        Worker idleWorker2 = new Worker("Bob", "Idle");
        entityManager.persist(assignedWorker);
        entityManager.persist(idleWorker1);
        entityManager.persist(idleWorker2);
        entityManager.flush();

        // Create an OPEN site with execution date 2026-01-20 and duration 5 days (ends 2026-01-24)
        Site site = createAndPersistSite(LocalDate.of(2026, 1, 20));
        site.setDurationInDays(5);
        site.setWorkers(List.of(assignedWorker));
        entityManager.merge(site);
        entityManager.flush();
        entityManager.clear();

        // Query date: 2026-01-22 (falls within the site's execution period)
        LocalDate queryDate = LocalDate.of(2026, 1, 22);

        // When - send GET request to get idle workers
        mockMvc.perform(get("/planning/idle")
                        .queryParam("date", queryDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-01-22"))
                .andExpect(jsonPath("$.idle_workers").isArray())
                .andExpect(jsonPath("$.idle_workers.length()").value(2))
                // Verify idle worker 1
                .andExpect(jsonPath("$.idle_workers[0].id").value(idleWorker1.getId()))
                .andExpect(jsonPath("$.idle_workers[0].first_name").value("Jane"))
                .andExpect(jsonPath("$.idle_workers[0].last_name").value("Idle"))
                // Verify idle worker 2
                .andExpect(jsonPath("$.idle_workers[1].id").value(idleWorker2.getId()))
                .andExpect(jsonPath("$.idle_workers[1].first_name").value("Bob"))
                .andExpect(jsonPath("$.idle_workers[1].last_name").value("Idle"));
    }

    @ParameterizedTest
    @MethodSource("invalidIsoDateFormats")
    void shouldReturnBadRequestWhenIdleWorkersDateIsNotInIsoFormat(String invalidDate) throws Exception {
        // When / Then - send GET request with invalid date format
        mockMvc.perform(get("/planning/idle")
                        .queryParam("date", invalidDate))
                .andExpect(status().isBadRequest());
    }

    private Site createAndPersistSite(LocalDate executionDate) {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setIsPrivate(false);

        Site site = new Site("Test Site", 5);
        site.setCustomer(customer);
        site.setCreationDate(Instant.now());

        if (executionDate != null) {
            site.setExecutionDate(executionDate);
        }

        entityManager.persist(site);
        entityManager.flush();
        entityManager.clear();

        return site;
    }
}
