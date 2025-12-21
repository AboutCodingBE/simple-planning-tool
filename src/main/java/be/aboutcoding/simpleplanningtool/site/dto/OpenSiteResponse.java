package be.aboutcoding.simpleplanningtool.site.dto;

import be.aboutcoding.simpleplanningtool.site.Site;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record OpenSiteResponse(
        Long id,
        String name,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("duration_in_days") Integer durationInDays,
        @JsonProperty("desired_date") LocalDate desiredDate,
        @JsonProperty("planned_date") LocalDate plannedDate
) {
    public static OpenSiteResponse from(Site site) {
        String customerName = site.getCustomer() != null
                ? site.getCustomer().getName()
                : null;

        return new OpenSiteResponse(
                site.getId(),
                site.getName(),
                customerName,
                site.getDurationInDays(),
                site.getDesiredDate(),
                site.getExecutionDate()
        );
    }
}
