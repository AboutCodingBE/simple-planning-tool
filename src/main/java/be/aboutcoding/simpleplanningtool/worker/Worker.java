package be.aboutcoding.simpleplanningtool.worker;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "workers")
@Data
@NoArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_creation")
    private Timestamp dateOfCreation;

    public Worker(String firstName, String lastName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name is mandatory");
        }
        if (lastName == null) {
            throw new IllegalArgumentException("Last name is mandatory");
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
