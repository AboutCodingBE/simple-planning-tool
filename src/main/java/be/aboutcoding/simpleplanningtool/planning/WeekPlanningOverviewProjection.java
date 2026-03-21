package be.aboutcoding.simpleplanningtool.planning;

public interface WeekPlanningOverviewProjection {
    Integer getMonth();
    String getMonthName();
    Integer getIsoWeek();
    Long[] getPlannedSiteIds();
    String[] getPlannedSiteNames();
    Long getSitesCount();
}
