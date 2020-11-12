package pl.dinosauruski.slot;

public enum DAY_OF_WEEK {

    MONDAY("poniedziałek"),
    TUESDAY("wtorek"),
    WEDNESDAY("środa"),
    THURSDAY("czwartek"),
    FRIDAY("piątek"),
    SATURDAY("sobota"),
    SUNDAY("niedziela");

    private String translation;


    DAY_OF_WEEK() {
    }

    DAY_OF_WEEK(String translation) {
        this.translation = translation;

    }

    public String getTranslation() {
        return translation;
    }

}
