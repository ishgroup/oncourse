package ish.oncourse.services.tutor;

import ish.oncourse.model.Tutor;

import java.util.Date;

public class GetIsActiveTutor {

    private Tutor tutor;

    private GetIsActiveTutor() {}

    public static GetIsActiveTutor valueOf(Tutor tutor) {
        GetIsActiveTutor obj = new GetIsActiveTutor();
        obj.tutor = tutor;
        return obj;
    }

    public boolean get() {
        return tutor.getFinishDate() == null || tutor.getFinishDate().after(new Date());
    }
}
