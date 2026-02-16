package be.aboutcoding.simpleplanningtool.planning.workerday;

import java.time.LocalDate;

public interface WorkerDayDetailProjection {
    Long getWorkerId();
    String getWorkerFirstName();
    String getWorkerLastName();
    String getLinkedSiteName();
    LocalDate getSiteUntil();
}
