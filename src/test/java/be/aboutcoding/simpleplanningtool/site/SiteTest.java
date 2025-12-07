package be.aboutcoding.simpleplanningtool.site;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SiteTest {

    @Test
    void shouldCreateSiteWithMandatoryFieldsButNoId() {
        // When
        Site site = new Site("Construction Site Alpha", 10);

        // Then
        assertThat(site.getName()).isEqualTo("Construction Site Alpha");
        assertThat(site.getDurationInDays()).isEqualTo(10);
        assertThat(site.getId()).isNull();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNameIsNull() {
        // When / Then
        assertThatThrownBy(() -> new Site(null, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenDurationInDaysIsNull() {
        // When / Then
        assertThatThrownBy(() -> new Site("Construction Site Alpha", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0, 1000, 1001, 2000})
    void shouldThrowIllegalArgumentExceptionWhenDurationInDaysIsInvalid(int invalidDuration) {
        // When / Then
        assertThatThrownBy(() -> new Site("Construction Site Alpha", invalidDuration))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
