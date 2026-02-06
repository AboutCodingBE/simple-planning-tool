package be.aboutcoding.simpleplanningtool.worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    @Query(value = """
            SELECT w.* FROM workers w
            WHERE NOT EXISTS (
                SELECT 1 FROM site_workers sw
                JOIN sites s ON sw.site_id = s.id
                WHERE sw.worker_id = w.id
                AND s.site_status = 'OPEN'
                AND s.execution_date <= :date
                AND :date <= (s.execution_date + s.duration_in_days - 1)
            )
            order by w.id asc
            """, nativeQuery = true)
    List<Worker> findIdleWorkersOnDate(@Param("date") LocalDate date);

    @Query(value = """
           SELECT id, first_name, last_name, date_of_creation FROM workers ORDER BY id asc;
            """, nativeQuery = true)
    List<Worker> findAllOrderedById();
}
