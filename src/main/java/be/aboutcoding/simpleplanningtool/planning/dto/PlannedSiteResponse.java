package be.aboutcoding.simpleplanningtool.planning.dto;

import be.aboutcoding.simpleplanningtool.planning.model.PlannedSite;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record PlannedSiteResponse(
        @JsonProperty("site_id") Long siteId,
        @JsonProperty("site_name") String siteName,
        @JsonProperty("execution_date") LocalDate executionDate,
        @JsonProperty("duration_in_days") int durationInDays,
        @JsonProperty("end_date") LocalDate endDate,
        @JsonProperty("days_remaining") int daysRemaining,
        @JsonProperty("site_status") String siteStatus,
        List<WorkerResponse> workers
) {
    public static PlannedSiteResponse from(PlannedSite plannedSite) {
        List<WorkerResponse> workers = plannedSite.getWorkers().stream()
                .map(WorkerResponse::from)
                .toList();
        return new PlannedSiteResponse(
                plannedSite.getSiteId(),
                plannedSite.getSiteName(),
                plannedSite.getExecutionDate(),
                plannedSite.getDurationInDays(),
                plannedSite.getEndDate(),
                plannedSite.getDaysRemaining(),
                plannedSite.getSiteStatus(),
                workers
        );
    }
}
