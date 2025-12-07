package be.aboutcoding.simpleplanningtool.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

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
}
