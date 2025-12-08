package be.aboutcoding.simpleplanningtool.site;

import be.aboutcoding.simpleplanningtool.site.dto.CreateSiteRequest;
import be.aboutcoding.simpleplanningtool.site.dto.CustomerResponse;
import be.aboutcoding.simpleplanningtool.site.dto.SiteResponse;
import be.aboutcoding.simpleplanningtool.site.dto.WorkerResponse;
import be.aboutcoding.simpleplanningtool.worker.Worker;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/sites")
public class SiteApi {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;

    public SiteApi(SiteRepository siteRepository, CustomerRepository customerRepository) {
        this.siteRepository = siteRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<Long> createSite(@Valid @RequestBody CreateSiteRequest request) {
        Site site = request.toSite();
        Site savedSite = siteRepository.save(site);
        return ResponseEntity.ok(savedSite.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        siteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable Long id) {
        return siteRepository.findById(id)
                .map(this::toSiteResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private SiteResponse toSiteResponse(Site site) {
        CustomerResponse customerResponse = toCustomerResponse(site.getCustomer());
        List<WorkerResponse> workerResponses = toWorkerResponses(site.getWorkers());

        LocalDate desiredDate = site.getDesiredDate() != null
                ? site.getDesiredDate()
                : null;

        LocalDate plannedDate = site.getExecutionDate() != null
                ? site.getExecutionDate()
                : null;

        return new SiteResponse(
                site.getId(),
                site.getName(),
                customerResponse,
                desiredDate,
                plannedDate,
                site.getCreationDate(),
                site.getDurationInDays(),
                workerResponses
        );
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getIsPrivate()
        );
    }

    private List<WorkerResponse> toWorkerResponses(List<Worker> workers) {
        if (workers == null) {
            return List.of();
        }
        return workers.stream()
                .map(worker -> new WorkerResponse(
                        worker.getId(),
                        worker.getFirstName(),
                        worker.getLastName()
                ))
                .toList();
    }
}
