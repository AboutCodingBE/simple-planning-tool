package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.model.MonthlyOverview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetMonthlyOverview {

    private final PlanningRepository planningRepository;
    private final MonthlyOverviewMapper mapper;

    public GetMonthlyOverview(PlanningRepository planningRepository, MonthlyOverviewMapper mapper) {
        this.planningRepository = planningRepository;
        this.mapper = mapper;
    }

    public List<MonthlyOverview> execute() {
        List<WeekPlanningOverviewProjection> rows = planningRepository.getWeekPlanningOverview();
        return mapper.map(rows);
    }
}
