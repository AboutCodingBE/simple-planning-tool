package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.dayplanning.RawPlannedSiteProjection;
import be.aboutcoding.simpleplanningtool.planning.workerday.WorkerDayDetailProjection;
import be.aboutcoding.simpleplanningtool.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanningRepository extends JpaRepository<Site, Long> {

    @Query(value = "SELECT * FROM get_active_sites_on_date(:date)", nativeQuery = true)
    List<RawPlannedSiteProjection> findActiveSitesOnDate(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM get_worker_day_overview(:date)", nativeQuery = true)
    List<WorkerDayDetailProjection> findWorkerDayOverview(@Param("date") LocalDate date);
}
