package be.aboutcoding.simpleplanningtool.worker;

public class WorkerNotFoundException extends RuntimeException {

    public WorkerNotFoundException(Long id) {
        super("Worker with id " + id + " not found");
    }
}
