package ish.oncourse.portal.services.dashboard

import groovy.transform.CompileStatic
import ish.oncourse.cache.ICacheFactory
import ish.oncourse.cache.ICacheProvider
import ish.oncourse.cache.caffeine.CaffeineFactory
import ish.oncourse.model.Attendance
import ish.oncourse.model.Session
import ish.oncourse.model.Student
import ish.oncourse.portal.services.attendance.AttendanceUtils
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.cache.Cache
import javax.cache.configuration.Configuration
import java.util.function.Supplier

@CompileStatic
class CalculateAttendancePercent {

    private static final Logger logger = LogManager.getLogger()

    public static final String DASHBOARD_CACHE = 'dashboard'
    public static final String ATTENDANCE_CACHE = 'attendance'

    public static final String ATTENDANCE_CACHE_KEY = 'dashboard.attendance.cache.%d'

    public static final int NO_ATTENDANCE = -1

    private Student student

    private ICacheProvider cacheProvider
    private ICacheFactory<String, Integer> cacheFactory
    private Configuration<String, Integer> cacheConfig


    CalculateAttendancePercent(Student student, ICacheProvider cacheProvider) {
        this.student = student
        this.cacheProvider = cacheProvider

        this.cacheFactory = cacheProvider.createFactory(String.class, Integer.class)
        long cacheSize = new Long(System.getProperty("cache.tapestry.size", "1000"))
        this.cacheConfig = CaffeineFactory.createDefaultConfig(String.class, Integer.class, cacheSize)
    }

    boolean isShowWiget() {
        return student && NO_ATTENDANCE != calculate()
    }
    
    int calculate() {
        String cacheKey = String.format(ATTENDANCE_CACHE_KEY, student.contact.id)

        Supplier<Integer> get = {
            Integer val = 0

            List<Attendance> attendances = getAttendance()
            if (attendances.empty) {
                val = NO_ATTENDANCE
            } else {
                val = AttendanceUtils.getAttendancePercent(getAttendance())
            }
            val
        }
        return getCachedAttendance(cacheKey, get)
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

    private Integer getCachedAttendance(String key, Supplier<Integer> get) {
        try {
            Cache<String, Integer> cache = cacheFactory.createIfAbsent(ATTENDANCE_CACHE, cacheConfig)
            Integer v = cache.get(key)
            if (v == null) {
                v = get.get()
                cache.put(key, v)
            }
            return v
        } catch (Exception e) {
            logger.error("Exception appeared during reading cached value. cacheGroup {}, key: {}", ATTENDANCE_CACHE, key);
            logger.catching(e);
            return get.get()
        }
    }
}
