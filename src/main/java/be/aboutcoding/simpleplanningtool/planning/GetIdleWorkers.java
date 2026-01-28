package be.aboutcoding.simpleplanningtool.planning;

import be.aboutcoding.simpleplanningtool.planning.dto.IdleWorkersResponse;
import be.aboutcoding.simpleplanningtool.worker.Worker;
import be.aboutcoding.simpleplanningtool.worker.WorkerRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GetIdleWorkers {

    private final WorkerRepository workerRepository;

    public GetIdleWorkers(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public List<Worker> execute(LocalDate date) {
        return workerRepository.findIdleWorkersOnDate(date);

    }
}
