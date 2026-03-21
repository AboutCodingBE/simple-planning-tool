package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.model.MonthlyOverview;
import be.aboutcoding.simpleplanningtool.planning.model.SiteReference;
import be.aboutcoding.simpleplanningtool.planning.model.WeekOverview;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MonthlyOverviewMapper {

    public List<MonthlyOverview> map(List<WeekPlanningOverviewProjection> rows) {
        Map<String, List<WeekOverview>> weeksByMonth = new LinkedHashMap<>();

        for (WeekPlanningOverviewProjection row : rows) {
            WeekOverview weekOverview = buildWeekOverview(row);
            weeksByMonth.computeIfAbsent(row.getMonthName(), k -> new ArrayList<>()).add(weekOverview);
        }

        return weeksByMonth.entrySet().stream()
                .map(entry -> new MonthlyOverview(entry.getKey(), entry.getValue()))
                .toList();
    }

    private WeekOverview buildWeekOverview(WeekPlanningOverviewProjection row) {
        List<SiteReference> sites = new ArrayList<>();

        if (row.getSitesCount() > 0) {
            Long[] siteIds = row.getPlannedSiteIds();
            String[] siteNames = row.getPlannedSiteNames();
            for (int i = 0; i < siteIds.length; i++) {
                sites.add(new SiteReference(siteIds[i], siteNames[i]));
            }
        }

        return new WeekOverview(row.getIsoWeek(), sites);
    }
}
