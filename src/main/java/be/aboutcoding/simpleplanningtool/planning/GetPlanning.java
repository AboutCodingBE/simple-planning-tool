package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.model.Planning;
import be.aboutcoding.simpleplanningtool.planning.model.SiteView;
import be.aboutcoding.simpleplanningtool.planning.model.Workday;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetPlanning {

    private final SiteRepository siteRepository;

    public GetPlanning(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public Planning execute(LocalDate from, LocalDate until) {
        // Fetch planning data from database
        List<PlanningDataProjection> planningData = siteRepository.getPlanningData(from, until);

        // Group by workday date and collect sites
        Map<LocalDate, WorkdayData> workdaysMap = new LinkedHashMap<>();

        for (PlanningDataProjection data : planningData) {
            LocalDate workdayDate = data.getWorkdayDate();

            // Get or create workday data
            WorkdayData workdayData = workdaysMap.get(workdayDate);
            if (workdayData == null) {
                workdayData = new WorkdayData(
                        workdayDate,
                        data.getWeekNumber(),
                        data.getDayOfWeek(),
                        new ArrayList<>()
                );
                workdaysMap.put(workdayDate, workdayData);
            }

            // Add site if it exists (site_id will be null for days with no sites)
            if (data.getSiteId() != null) {
                SiteView siteView = new SiteView(
                        data.getSiteId(),
                        data.getSiteName(),
                        data.getDurationInDays(),
                        data.getSiteStatus()
                );
                workdayData.sites.add(siteView);
            }
        }

        // Convert to WorkdayResponse records
        List<Workday> workdays = workdaysMap.values().stream()
                .map(wd -> new Workday(wd.date, wd.week, wd.dayOfWeek, wd.sites))
                .toList();

        return new Planning(from, until, workdays);
    }

    // Helper class for grouping data before creating immutable records
    private static class WorkdayData {
        LocalDate date;
        Integer week;
        String dayOfWeek;
        List<SiteView> sites;

        WorkdayData(LocalDate date, Integer week, String dayOfWeek, List<SiteView> sites) {
            this.date = date;
            this.week = week;
            this.dayOfWeek = dayOfWeek;
            this.sites = sites;
        }
    }
}
