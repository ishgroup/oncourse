package ish.oncourse.portal.services.dashboard

import groovy.transform.CompileStatic
import ish.oncourse.model.Attendance
import ish.oncourse.model.Session
import ish.oncourse.model.Student
import ish.oncourse.portal.services.attendance.AttendanceUtils
import org.apache.cayenne.query.ObjectSelect

import javax.cache.Cache
import javax.cache.CacheManager

import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE
import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE

@CompileStatic
class CalculateAttendancePercent {

    public static final String DASHBOARD_CACHE = 'dashboard'
    public static final String ATTENDANCE_CACHE = 'attendance'

    public static final String ATTENDANCE_CACHE_KEY = 'dashboard.attendance.cache.%d'

    public static final int NO_ATTENDANCE = -1

    private Student student
    private CacheManager cacheManager

    CalculateAttendancePercent(Student student, CacheManager cacheManager) {
        this.student = student
        this.cacheManager = cacheManager
    }

    boolean isShowWiget() {
        return student && NO_ATTENDANCE != calculate()
    }
    
    int calculate() {
        Cache<String, Integer> cache = cacheManager.getCache(ATTENDANCE_CACHE, String, Integer)
        String cacheKey = String.format(ATTENDANCE_CACHE_KEY, student.contact.id)

        Integer value = cache.get(cacheKey)

        if (value == null) {
            List<Attendance> attendances = getAttendance()
            if (attendances.empty) {
                value = NO_ATTENDANCE
            } else {
                value = AttendanceUtils.getAttendancePercent(getAttendance())
            }
            cache.put(cacheKey, value)
            return value
        } else {
            return value
        }
    }


    List<Attendance> getAttendance() {

        Date now = new Date()
        
        Calendar yearAgo = Calendar.instance
        yearAgo.setTime(new Date())
        yearAgo.add(Calendar.YEAR, -1)

        return ObjectSelect.query(Attendance).where(Attendance.STUDENT.eq(student))
                .and(Attendance.SESSION.dot(Session.START_DATE).between(yearAgo.time, now))
                .prefetch(Attendance.SESSION.disjoint())
                .select(student.objectContext)
    }
}
