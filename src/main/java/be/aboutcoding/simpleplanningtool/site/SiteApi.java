package be.aboutcoding.simpleplanningtool.site;

import be.aboutcoding.simpleplanningtool.site.dto.CreateSiteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
        // Create and save customer
        Customer customer = new Customer();
        customer.setName(request.customerName());
        customer.setIsPrivate(request.isPrivateCustomer() != null ? request.isPrivateCustomer() : false);

        // Create and save site
        Site site = new Site();
        site.setName(request.name());
        site.setCustomer(customer);
        site.setCreationDate(Instant.now());

        if (request.desiredDate() != null) {
            site.setDesiredDate(request.desiredDate());
        }

        if (request.durationInDays() != null) {
            site.setDurationInDays(request.durationInDays());
        }

        if (request.transport() != null) {
            site.setTransport(request.transport());
        }

        Site savedSite = siteRepository.save(site);

        return ResponseEntity.ok(savedSite.getId());
    }
}
