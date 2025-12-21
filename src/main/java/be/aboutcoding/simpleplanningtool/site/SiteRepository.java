package be.aboutcoding.simpleplanningtool.site;

import be.aboutcoding.simpleplanningtool.planning.PlanningDataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query(value = "SELECT * FROM get_planning(:fromDate, :untilDate)", nativeQuery = true)
    List<PlanningDataProjection> getPlanningData(
            @Param("fromDate") LocalDate fromDate,
            @Param("untilDate") LocalDate untilDate
    );

    List<Site> findByStatus(SiteStatus status);
}
