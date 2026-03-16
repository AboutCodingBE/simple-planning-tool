package be.aboutcoding.simpleplanningtool.planning;

public class WeekInThePastException extends RuntimeException {

    public WeekInThePastException(int week, int year) {
        super("Week " + week + " of " + year + " is in the past");
    }
}
