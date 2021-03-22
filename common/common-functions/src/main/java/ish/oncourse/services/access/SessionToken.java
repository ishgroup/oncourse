package ish.oncourse.services.access;


import java.io.Serializable;

public class SessionToken implements Serializable {

    public static final String SESSION_TOKEN_KEY = "session_token_key";

    private Long collegeId;
    private Long communicationKey;

    public SessionToken(Long collegeId, Long communicationKey) {
        this.collegeId = collegeId;
        this.communicationKey = communicationKey;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getCommunicationKey() {
        return communicationKey;
    }

    public void setCommunicationKey(Long communicationKey) {
        this.communicationKey = communicationKey;
    }
}