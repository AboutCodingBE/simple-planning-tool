package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.dayplanning.DayPlanningFlowController;
import be.aboutcoding.simpleplanningtool.planning.dto.DayOverviewResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.IdleWorkersResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.PlanningResponse;
import be.aboutcoding.simpleplanningtool.planning.dto.WorkerDayOverviewResponse;
import be.aboutcoding.simpleplanningtool.planning.model.DayOverview;
import be.aboutcoding.simpleplanningtool.planning.model.Planning;
import be.aboutcoding.simpleplanningtool.planning.workerday.GetDayOverviewWorkers;
import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/planning")
@Transactional
public class PlanningApi {

    private final SiteRepository siteRepository;
    private final LinkWorkerToSite linkWorkerToSite;
    private final UnlinkWorker unlinkWorker;
    private final GetPlanning getPlanning;
    private final PlanningResponseMapper planningResponseMapper;
    private final DayPlanningFlowController dayPlanningFlowController;
    private final GetIdleWorkers getIdleWorkers;
    private final GetDayOverviewWorkers getDayOverviewWorkers;

    public PlanningApi(SiteRepository siteRepository, LinkWorkerToSite linkWorkerToSite, UnlinkWorker unlinkWorker,
                       GetPlanning getPlanning, PlanningResponseMapper planningResponseMapper,
                       DayPlanningFlowController dayPlanningFlowController, GetIdleWorkers getIdleWorkers,
                       GetDayOverviewWorkers getDayOverviewWorkers) {
        this.siteRepository = siteRepository;
        this.linkWorkerToSite = linkWorkerToSite;
        this.unlinkWorker = unlinkWorker;
        this.getPlanning = getPlanning;
        this.planningResponseMapper = planningResponseMapper;
        this.dayPlanningFlowController = dayPlanningFlowController;
        this.getIdleWorkers = getIdleWorkers;
        this.getDayOverviewWorkers = getDayOverviewWorkers;
    }

    @PatchMapping("/sites/{siteId}")
    public ResponseEntity<Void> planSite(
            @PathVariable Long siteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Check if date is in the past
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().build();
        }

        // Find the site
        Site site = siteRepository.findById(siteId)
                .orElse(null);

        if (site == null) {
            return ResponseEntity.notFound().build();
        }

        // Update execution date
        site.setExecutionDate(date);
        siteRepository.save(site);
        siteRepository.flush();

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sites/{siteId}/workers")
    public ResponseEntity<Void> linkWorkerToSite(
            @PathVariable Long siteId,
            @RequestParam Long workerId) {

        linkWorkerToSite.execute(siteId, workerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sites/{siteId}/unlink")
    public ResponseEntity<Void> unlinkWorkerFromSite(
            @PathVariable Long siteId,
            @RequestParam Long workerId) {

        unlinkWorker.execute(siteId, workerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PlanningResponse> getPlanning(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until) {

        // Apply defaults
        LocalDate fromDate = from != null ? from : LocalDate.now();
        LocalDate untilDate = until != null ? until : fromDate.plusDays(30);

        // Validate: until cannot be before from
        if (untilDate.isBefore(fromDate)) {
            return ResponseEntity.badRequest().build();
        }

        Planning planning = getPlanning.execute(fromDate, untilDate);
        PlanningResponse response = planningResponseMapper.toResponse(planning);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day")
    public ResponseEntity<DayOverviewResponse> getDayOverview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DayOverview dayOverview = dayPlanningFlowController.execute(date);
        DayOverviewResponse response = DayOverviewResponse.from(dayOverview);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/idle")
    public ResponseEntity<IdleWorkersResponse> getIdleWorkers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        var idleWorkers = getIdleWorkers.execute(date);
        var response =  IdleWorkersResponse.from(date, idleWorkers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/worker/day")
    public ResponseEntity<WorkerDayOverviewResponse> getWorkerDayOverview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        WorkerDayOverviewResponse response = getDayOverviewWorkers.execute(date);
        return ResponseEntity.ok(response);
    }
}
