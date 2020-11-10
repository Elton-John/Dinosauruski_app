package pl.dinosauruski.slot;

public enum DAY_OF_WEEK {

    MON("poniedziałek"),
    TUE("wtorek"),
    WED("środa"),
    THU("czwartek"),
    FRI("piątek"),
    SAT("sobota"),
    SUN("niedziela");

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
