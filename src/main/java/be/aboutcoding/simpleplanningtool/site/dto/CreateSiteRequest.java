package be.aboutcoding.simpleplanningtool.site.dto;

import be.aboutcoding.simpleplanningtool.site.Customer;
import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;

public record CreateSiteRequest(
        @JsonProperty("name")
        @NotBlank(message = "Name is mandatory")
        String name,

        @JsonProperty("customer_name")
        @NotBlank(message = "Customer name is mandatory")
        String customerName,

        @JsonProperty("is_private_customer")
        Boolean isPrivateCustomer,

        @JsonProperty("desired_date")
        LocalDate desiredDate,

        @JsonProperty("duration_in_days")
        Integer durationInDays,

        @JsonProperty("transport")
        String transport
) {
    public Site toSite() {
        // Create customer
        Customer customer = new Customer();
        customer.setName(customerName);
        customer.setIsPrivate(isPrivateCustomer != null ? isPrivateCustomer : false);

        // Create site
        Site site = new Site();
        site.setName(name);
        site.setCustomer(customer);
        site.setCreationDate(Instant.now());

        if (desiredDate != null) {
            site.setDesiredDate(desiredDate);
        }

        if (durationInDays != null) {
            site.setDurationInDays(durationInDays);
        }

        if (transport != null) {
            site.setTransport(transport);
        }
        site.setStatus(SiteStatus.OPEN);

        return site;
    }
}
