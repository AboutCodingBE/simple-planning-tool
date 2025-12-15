package be.aboutcoding.simpleplanningtool.site;

import be.aboutcoding.simpleplanningtool.worker.Worker;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sites")
@Data
@NoArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "desired_date")
    private LocalDate desiredDate;

    @Column(name = "execution_date")
    private LocalDate executionDate;

    @Column(name = "duration_in_days")
    private Integer durationInDays;

    @Column(name = "transport")
    private String transport;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "site_status")
    @Enumerated(EnumType.STRING)
    private SiteStatus status;

    @ManyToMany
    @JoinTable(
            name = "site_workers",
            joinColumns = @JoinColumn(name = "site_id"),
            inverseJoinColumns = @JoinColumn(name = "worker_id")
    )
    private List<Worker> workers;

    public Site(String name, Integer durationInDays) {
        if (name == null) {
            throw new IllegalArgumentException("Name is mandatory");
        }
        if (durationInDays == null) {
            throw new IllegalArgumentException("Duration in days is mandatory");
        }
        if (durationInDays <= 0 || durationInDays >= 1000) {
            throw new IllegalArgumentException("Duration in days needs to be a positive number and < 1000");
        }
        this.name = name;
        this.durationInDays = durationInDays;
        this.status = SiteStatus.OPEN;
    }
}
