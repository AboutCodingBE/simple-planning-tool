package be.aboutcoding.simpleplanningtool.planning.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PlannedSite {
    private final Long siteId;
    private final String siteName;
    private final LocalDate executionDate;
    private final int durationInDays;
    private final LocalDate endDate;
    private final int daysRemaining;
    private final String siteStatus;
    private final List<WorkerView> workers;

    public PlannedSite(Long siteId, String siteName, LocalDate executionDate, int durationInDays,
                       LocalDate endDate, int daysRemaining, String siteStatus) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.executionDate = executionDate;
        this.durationInDays = durationInDays;
        this.endDate = endDate;
        this.daysRemaining = daysRemaining;
        this.siteStatus = siteStatus;
        this.workers = new ArrayList<>();
    }

    public void addWorker(WorkerView worker) {
        this.workers.add(worker);
    }
}
