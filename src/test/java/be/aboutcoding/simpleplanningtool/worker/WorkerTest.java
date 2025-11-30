package be.aboutcoding.simpleplanningtool.worker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkerTest {

    @Test
    void shouldCreateWorkerWithFirstNameAndLastNameButNoIdAndNoDateOfCreation() {
        // When
        Worker worker = new Worker("John", "Doe");

        // Then
        assertThat(worker.getFirstName()).isEqualTo("John");
        assertThat(worker.getLastName()).isEqualTo("Doe");
        assertThat(worker.getId()).isNull();
        assertThat(worker.getDateOfCreation()).isNull();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenFirstNameIsNull() {
        // When / Then
        assertThatThrownBy(() -> new Worker(null, "Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is mandatory");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenLastNameIsNull() {
        // When / Then
        assertThatThrownBy(() -> new Worker("John", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name is mandatory");
    }
}
