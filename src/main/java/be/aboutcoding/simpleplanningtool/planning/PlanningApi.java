package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/planning")
public class PlanningApi {

    private final SiteRepository siteRepository;

    public PlanningApi(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
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

        return ResponseEntity.noContent().build();
    }
}
