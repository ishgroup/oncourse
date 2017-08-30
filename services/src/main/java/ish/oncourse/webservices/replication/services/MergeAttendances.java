package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Student;
import org.apache.cayenne.ObjectContext;

import java.util.*;

/**
 * Safe merge. Attendances that belongs to the same course class merges according to theirs priority
 * Created by pavel on 4/17/17.
 */
public class MergeAttendances {

    private ObjectContext context;
    private Student update;
    private Student delete;

    public static MergeAttendances valueOf(ObjectContext context, Student upd, Student del) {
        MergeAttendances res = new MergeAttendances();
        res.context = context;
        res.update = upd;
        res.delete = del;
        return res;
    }

    private MergeAttendances() {
    }

    public void merge() {
        List<Attendance> removeList = new ArrayList<>();
        List<Attendance> toUpdateAttendances = new ArrayList<>();

        for (Attendance aToDel : delete.getAttendances()) {
            Attendance aToUpd = null;
            for (Attendance a : update.getAttendances()) {
                if (a.getSession().getId() == aToDel.getSession().getId()) {
                    aToUpd = a;
                    break;
                }
            }
            if (aToUpd != null) {
                if (compare(aToUpd, aToDel) == 1) {
                    copyAttendanceToUpdatable(aToUpd, aToDel);
                }
                removeList.add(aToDel);
            } else {
                toUpdateAttendances.add(aToDel);
            }
        }

        for(int i = 0; i < toUpdateAttendances.size(); i++){
            toUpdateAttendances.get(i).setStudent(update);
        }
        context.deleteObjects(removeList);
    }

    //if a < b returns 1, a > b returns -1, a == b returns 0
    private int compare(Attendance a, Attendance b) {
        Integer[] v = new Integer[]{0, 3, 2, 4, 1};
        List<Integer> compareVector = Arrays.asList(v);
        if (!a.equals(b)) {
            if (compareVector.indexOf(a.getAttendanceType()) < compareVector.indexOf(b.getAttendanceType()))
                return 1;
            else
                return -1;
        }
        return 0;
    }

    private void copyAttendanceToUpdatable(Attendance upd, Attendance del) {
        upd.setAttendanceType(del.getAttendanceType());
        upd.setNote(del.getNote());
        upd.setDurationMinutes(del.getDurationMinutes());
        upd.setMarkedByTutor(del.getMarkedByTutor());
        upd.setMarkedByTutorDate(del.getMarkedByTutorDate());
        upd.setCreated(del.getCreated());
        upd.setAttendedFrom(del.getAttendedFrom());
        upd.setAttendedUntil(del.getAttendedUntil());
    }
}
