package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.SiteNotFoundException;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

@Component
public class PlanSiteForWeek {

    private final SiteRepository siteRepository;
    private final PlanningRepository planningRepository;

    public PlanSiteForWeek(SiteRepository siteRepository, PlanningRepository planningRepository) {
        this.siteRepository = siteRepository;
        this.planningRepository = planningRepository;
    }

    @Transactional
    public void execute(int week, int year, Long siteId) {
        siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException(siteId));

        planningRepository.planSiteForWeek(week, year, siteId);
    }
}
