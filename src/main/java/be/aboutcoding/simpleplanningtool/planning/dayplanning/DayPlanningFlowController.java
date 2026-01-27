package be.aboutcoding.simpleplanningtool.planning.dayplanning;

import be.aboutcoding.simpleplanningtool.planning.PlanningRepository;
import be.aboutcoding.simpleplanningtool.planning.model.DayOverview;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DayPlanningFlowController {

    private final PlanningRepository planningRepository;
    private final PlannedSiteMapper plannedSiteMapper;

    public DayPlanningFlowController(PlanningRepository planningRepository, PlannedSiteMapper plannedSiteMapper) {
        this.planningRepository = planningRepository;
        this.plannedSiteMapper = plannedSiteMapper;
    }

    public DayOverview execute(LocalDate date) {
        List<RawPlannedSiteProjection> rawData = planningRepository.findActiveSitesOnDate(date);
        return plannedSiteMapper.toDayOverview(date, rawData);
    }
}
