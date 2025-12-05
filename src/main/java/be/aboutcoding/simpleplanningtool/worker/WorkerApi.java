package be.aboutcoding.simpleplanningtool.worker;

import be.aboutcoding.simpleplanningtool.worker.dto.CreateWorkerRequest;
import be.aboutcoding.simpleplanningtool.worker.dto.UpdateWorkerRequest;
import be.aboutcoding.simpleplanningtool.worker.dto.WorkerResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponse> getWorker(@PathVariable Long id) {
        return workerRepository.findById(id)
                .map(worker -> ResponseEntity.ok(new WorkerResponse(
                        worker.getId(),
                        worker.getFirstName(),
                        worker.getLastName(),
                        worker.getDateOfCreation()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateWorker(@PathVariable Long id, @Valid @RequestBody UpdateWorkerRequest request) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException(id));

        worker.setFirstName(request.firstName());
        worker.setLastName(request.lastName());
        workerRepository.save(worker);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
