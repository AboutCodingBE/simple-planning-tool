package be.aboutcoding.simpleplanningtool.planning.workerday;

import be.aboutcoding.simpleplanningtool.planning.dto.CurrentSiteDto;
import be.aboutcoding.simpleplanningtool.planning.dto.WorkerDayDetailDto;
import be.aboutcoding.simpleplanningtool.planning.dto.WorkerDayOverviewResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class WorkerDayOverviewMapper {

    public WorkerDayOverviewResponse toResponse(LocalDate date, List<WorkerDayDetailProjection> projections) {
        List<WorkerDayDetailDto> dayOverview = projections.stream()
                .map(this::toWorkerDayDetailDto)
                .toList();

        return new WorkerDayOverviewResponse(date, dayOverview);
    }

    private WorkerDayDetailDto toWorkerDayDetailDto(WorkerDayDetailProjection projection) {
        CurrentSiteDto currentSite = null;

        if (projection.getLinkedSiteName() != null) {
            currentSite = new CurrentSiteDto(
                    projection.getLinkedSiteName(),
                    projection.getSiteUntil()
            );
        }

        return new WorkerDayDetailDto(
                projection.getWorkerId(),
                projection.getWorkerFirstName(),
                projection.getWorkerLastName(),
                currentSite
        );
    }
}
