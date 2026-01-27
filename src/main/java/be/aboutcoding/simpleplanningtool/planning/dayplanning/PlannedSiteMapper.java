package be.aboutcoding.simpleplanningtool.planning.dayplanning;

import be.aboutcoding.simpleplanningtool.planning.model.DayOverview;
import be.aboutcoding.simpleplanningtool.planning.model.PlannedSite;
import be.aboutcoding.simpleplanningtool.planning.model.WorkerView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class PlannedSiteMapper {

    public DayOverview toDayOverview(LocalDate date, List<RawPlannedSiteProjection> rawData) {
        Map<Long, PlannedSite> plannedSiteMap = new HashMap<>();

        for (RawPlannedSiteProjection raw : rawData) {
            Long siteId = raw.getSiteId();

            PlannedSite site = plannedSiteMap.get(siteId);
            if (site == null) {
                site = new PlannedSite(
                        raw.getSiteId(),
                        raw.getSiteName(),
                        raw.getExecutionDate(),
                        raw.getDurationInDays(),
                        raw.getEndDate(),
                        raw.getDaysRemaining(),
                        raw.getSiteStatus());
                plannedSiteMap.put(siteId, site);
            }

            if (raw.getWorkerId() != null) {
                WorkerView worker = new WorkerView(raw.getWorkerId(), raw.getWorkerFirstName(), raw.getWorkerLastName());
                site.addWorker(worker);
            }
        }

        return new DayOverview(date, plannedSiteMap.values().stream().toList());
    }
}
