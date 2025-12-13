package be.aboutcoding.simpleplanningtool.site.dto;

import be.aboutcoding.simpleplanningtool.site.Customer;
import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.worker.Worker;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SiteResponse(
        Long id,
        String name,
        CustomerResponse customer,
        @JsonProperty("desired_date") LocalDate desiredDate,
        @JsonProperty("planned_date") LocalDate plannedDate,
        @JsonProperty("creation_date") Instant creationDate,
        @JsonProperty("duration_in_days") Integer durationInDays,
        List<WorkerResponse> workers
) {
    public static SiteResponse from(Site site) {
        CustomerResponse customerResponse = from(site.getCustomer());
        List<WorkerResponse> workers = from(site.getWorkers());

        return new SiteResponse(
                site.getId(),
                site.getName(),
                customerResponse,
                site.getDesiredDate(),
                site.getExecutionDate(),
                site.getCreationDate(),
                site.getDurationInDays(),
                workers
        );
    }

    private static CustomerResponse from(Customer customer) {
        if (customer == null) {
            return null;
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getIsPrivate()
        );
    }

    private static List<WorkerResponse> from(List<Worker> workers){
        if (workers == null) {
            return List.of();
        }

        return workers.stream()
                .map(worker -> new WorkerResponse(
                        worker.getId(),
                        worker.getFirstName(),
                        worker.getFirstName()))
                .toList();
    }

}
