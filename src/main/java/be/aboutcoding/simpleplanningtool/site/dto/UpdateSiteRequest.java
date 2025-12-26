package be.aboutcoding.simpleplanningtool.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateSiteRequest(
        @JsonProperty("name")
        @NotBlank(message = "Name is mandatory")
        String name,

        @JsonProperty("customer_name")
        @NotBlank(message = "Customer name is mandatory")
        String customerName,

        @JsonProperty("is_private_customer")
        @NotNull(message = "is_private_customer is mandatory")
        Boolean isPrivateCustomer,

        @JsonProperty("desired_date")
        LocalDate desiredDate,

        @JsonProperty("duration_in_days")
        @NotNull(message = "Duration in days is mandatory")
        Integer durationInDays,

        @JsonProperty("transport")
        String transport
) {
}
