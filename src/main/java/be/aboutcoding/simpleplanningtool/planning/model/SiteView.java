package be.aboutcoding.simpleplanningtool.planning.model;

public record SiteView(
        Long id,
        String name,
        Integer durationInDays,
        String status
) {
}
