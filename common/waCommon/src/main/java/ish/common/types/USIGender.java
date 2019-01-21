package ish.common.types;

public enum USIGender {
    MALE("M"),
    FEMALE("F"),
    UNSPECIFIED("X");

    private String code;

    USIGender(String code) {
        this.code = code;
    }

    public String getRequestCode() {
        return this.code;
    }
}
