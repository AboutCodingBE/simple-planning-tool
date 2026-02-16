package be.aboutcoding.simpleplanningtool.planning.workerday;

import be.aboutcoding.simpleplanningtool.planning.PlanningRepository;
import be.aboutcoding.simpleplanningtool.planning.dto.WorkerDayOverviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GetDayOverviewWorkers {

    private final PlanningRepository planningRepository;
    private final WorkerDayOverviewMapper mapper;

    public GetDayOverviewWorkers(PlanningRepository planningRepository, WorkerDayOverviewMapper mapper) {
        this.planningRepository = planningRepository;
        this.mapper = mapper;
    }

    public WorkerDayOverviewResponse execute(LocalDate date) {
        List<WorkerDayDetailProjection> projections = planningRepository.findWorkerDayOverview(date);
        return mapper.toResponse(date, projections);
    }
}
