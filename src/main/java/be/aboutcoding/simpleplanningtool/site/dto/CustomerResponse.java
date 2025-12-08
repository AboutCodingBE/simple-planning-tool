package be.aboutcoding.simpleplanningtool.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerResponse(
        Long id,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("is_private_customer") Boolean isPrivateCustomer
) {
}
