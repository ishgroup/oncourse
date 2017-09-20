package ish.oncourse.portal.services.dashboard

import groovy.transform.CompileStatic
import ish.oncourse.model.Attendance
import ish.oncourse.model.Session
import ish.oncourse.model.Student
import ish.oncourse.portal.services.attendance.AttendanceUtils
import org.apache.cayenne.query.ObjectSelect

import javax.cache.Cache
import javax.cache.CacheManager

import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE

@CompileStatic
class CalculateAttendancePercent {

    public static final String DASHBOARD_CACHE = 'dashboard'

    public static final String ATTENDANCE_CACHE_KEY = 'dashboard.attendance.cache.%d'

    private Student student
    private CacheManager cacheManager
    private List<Attendance> attendances

    CalculateAttendancePercent(Student student, CacheManager cacheManager) {
        this.student = student
        this.cacheManager = cacheManager
    }

    int calculate() {
        Cache<String, Integer> cache = cacheManager.getCache(DASHBOARD_CACHE)
        String cacheKey = String.format(ATTENDANCE_CACHE_KEY, student.contact.id)

        Integer value = cache.get(cacheKey)

        if (value == null) {
            value = AttendanceUtils.getAttendancePercent(getAttendance())
            cache.put(cacheKey, value)
            return value
        } else {
            return value
        }
    }


    List<Attendance> getAttendance() {
        if (!student) {
            return Collections.EMPTY_LIST
        } else if (attendances == null) {

            Date now = new Date()

            Calendar yearAgo = Calendar.instance
            yearAgo.setTime(new Date())
            yearAgo.add(Calendar.YEAR, -1)

            attendances = ObjectSelect.query(Attendance).where(Attendance.STUDENT.eq(student))
                    .and(Attendance.SESSION.dot(Session.START_DATE).between(yearAgo.time, now))
                    .prefetch(Attendance.SESSION.disjoint())
                    .cacheStrategy(SHARED_CACHE, DASHBOARD_CACHE)
                    .select(student.objectContext)
        }
        return attendances
    }
}
