package be.aboutcoding.simpleplanningtool.planning.dto;

import java.time.LocalDate;

public record CurrentSiteDto(
        String name,
        LocalDate until
) {
}
