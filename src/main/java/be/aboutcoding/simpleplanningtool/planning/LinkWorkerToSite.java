package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.site.Site;
import be.aboutcoding.simpleplanningtool.site.SiteHasNoExecutionDateException;
import be.aboutcoding.simpleplanningtool.site.SiteNotFoundException;
import be.aboutcoding.simpleplanningtool.site.SiteRepository;
import be.aboutcoding.simpleplanningtool.worker.Worker;
import be.aboutcoding.simpleplanningtool.worker.WorkerNotFoundException;
import be.aboutcoding.simpleplanningtool.worker.WorkerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LinkWorkerToSite {

    private final SiteRepository siteRepository;
    private final WorkerRepository workerRepository;

    public LinkWorkerToSite(SiteRepository siteRepository, WorkerRepository workerRepository) {
        this.siteRepository = siteRepository;
        this.workerRepository = workerRepository;
    }

    public void execute(Long siteId, Long workerId) {
        // Find the site
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException(siteId));

        // Check if site has an execution date
        if (site.getExecutionDate() == null) {
            throw new SiteHasNoExecutionDateException(siteId);
        }

        // Find the worker
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkerNotFoundException(workerId));

        // Link worker to site
        if (site.getWorkers() == null) {
            site.setWorkers(new ArrayList<>());
        }
        if (!site.getWorkers().contains(worker)) {
            site.getWorkers().add(worker);
        }
        siteRepository.save(site);
    }
}
