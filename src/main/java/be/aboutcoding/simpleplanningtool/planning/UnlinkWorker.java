package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteNotFoundException;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import org.springframework.stereotype.Component;

@Component
public class UnlinkWorker {

    private final SiteRepository siteRepository;

    public UnlinkWorker(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public void execute(Long siteId, Long workerId) {
        // Find the site
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException(siteId));

        // Remove worker from site if workers list exists
        if (site.getWorkers() != null) {
            site.getWorkers().removeIf(worker -> worker.getId().equals(workerId));
        }

        siteRepository.save(site);
    }
}
