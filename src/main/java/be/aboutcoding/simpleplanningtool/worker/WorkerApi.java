package be.aboutcoding.simpleplanningtool.worker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/workers")
public class WorkerApi {

    private final WorkerRepository workerRepository;

    public WorkerApi(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @PostMapping
    public ResponseEntity<Long> createWorker(@RequestBody CreateWorkerRequest request) {
        if (request.firstName() == null || request.lastName() == null) {
            return ResponseEntity.badRequest().build();
        }

        Worker worker = new Worker(request.firstName(), request.lastName());
        worker.setDateOfCreation(Timestamp.from(Instant.now()));

        Worker savedWorker = workerRepository.save(worker);

        return ResponseEntity.ok(savedWorker.getId());
    }
}
