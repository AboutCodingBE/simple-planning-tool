package be.aboutcoding.simpleplanningtool.site;

public class SiteNotFoundException extends RuntimeException {

    public SiteNotFoundException(Long id) {
        super("Site with id " + id + " not found");
    }
}
