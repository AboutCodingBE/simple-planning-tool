package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.dto.DayResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.PlanningResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.SiteViewResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.WeekResponse;
import be.aboutcoding.simpleplanningtool.planning.model.Planning;
import be.aboutcoding.simpleplanningtool.planning.model.SiteView;
import be.aboutcoding.simpleplanningtool.planning.model.Workday;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlanningResponseMapper {

    public PlanningResponse toResponse(Planning planning) {
        // Group workdays by week number
        Map<Integer, List<Workday>> weekGroups = new LinkedHashMap<>();

        for (Workday workday : planning.workdays()) {
            weekGroups
                    .computeIfAbsent(workday.week(), k -> new ArrayList<>())
                    .add(workday);
        }

        // Convert each week group to WeekResponse
        List<WeekResponse> weeks = new ArrayList<>();
        for (Map.Entry<Integer, List<Workday>> entry : weekGroups.entrySet()) {
            Integer weekNumber = entry.getKey();
            List<Workday> workdays = entry.getValue();

            // Create a map of day name to workday for easy lookup
            Map<String, Workday> dayMap = new HashMap<>();
            for (Workday workday : workdays) {
                dayMap.put(workday.dayOfWeek(), workday);
            }

            // Create DayResponse for each day of the week
            DayResponse monday = createDayResponse(dayMap.get("Monday"));
            DayResponse tuesday = createDayResponse(dayMap.get("Tuesday"));
            DayResponse wednesday = createDayResponse(dayMap.get("Wednesday"));
            DayResponse thursday = createDayResponse(dayMap.get("Thursday"));
            DayResponse friday = createDayResponse(dayMap.get("Friday"));
            DayResponse saturday = createDayResponse(dayMap.get("Saturday"));
            DayResponse sunday = createDayResponse(dayMap.get("Sunday"));

            WeekResponse weekResponse = new WeekResponse(
                    weekNumber,
                    monday,
                    tuesday,
                    wednesday,
                    thursday,
                    friday,
                    saturday,
                    sunday
            );

            weeks.add(weekResponse);
        }

        return new PlanningResponse(
                planning.from(),
                planning.until(),
                weeks
        );
    }

    private DayResponse createDayResponse(Workday workday) {
        if (workday == null) {
            // This shouldn't happen if database function generates all days
            return null;
        }

        List<SiteViewResponse> sites = workday.sites().stream()
                .map(this::toSiteViewResponse)
                .toList();

        return new DayResponse(workday.date(), sites);
    }

    private SiteViewResponse toSiteViewResponse(SiteView siteView) {
        return new SiteViewResponse(
                siteView.id(),
                siteView.name(),
                siteView.durationInDays(),
                siteView.status()
        );
    }
}
