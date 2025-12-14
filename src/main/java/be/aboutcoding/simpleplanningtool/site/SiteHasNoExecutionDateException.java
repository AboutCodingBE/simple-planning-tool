package be.aboutcoding.simpleplanningtool.site;

public class SiteHasNoExecutionDateException extends RuntimeException {

    public SiteHasNoExecutionDateException(Long id) {
        super("Site with id " + id + " has no execution date");
    }
}
