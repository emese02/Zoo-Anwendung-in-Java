package domain;

/**
 * Enumeration for the days of the week.
 */
public enum Weekday {
    MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);

    /**
     * Each weekday has a number which shows the order of the days in a week.
     */
    private final int nr;

    /**
     * Private constructor - constructs and initializes the weekday.
     * @param nr Integer - number of day in the week
     */
    Weekday(int nr) {
        this.nr = nr;
    }

    /**
     * This method returns the number of the weekday.
     * @return Integer - the number of the weekday
     */
    public int getNr() {
        return nr;
    }
}
