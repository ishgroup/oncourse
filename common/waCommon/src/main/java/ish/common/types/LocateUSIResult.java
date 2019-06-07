package ish.common.types;

public class LocateUSIResult {

    private LocateUSIType resultType;
    private String usi;
    private String message;
    private String error;

    public LocateUSIType getResultType() {
        return resultType;
    }

    public void setResultType(LocateUSIType resultType) {
        this.resultType = resultType;
    }

    public String getUsi() {
        return usi;
    }

    public void setUsi(String usi) {
        this.usi = usi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
