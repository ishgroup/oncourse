package ish.oncourse.services.courseclass.functions;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.ArrayList;
import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

/**
 * Created by pavel on 3/27/17.
 */
public class GetCourseByCode {

    private ObjectContext context;
    private College college;
    private String code;

    private GetCourseByCode (){}

    public static GetCourseByCode valueOf(ObjectContext context, College college, String code){
        GetCourseByCode res = new GetCourseByCode();
        res.context = context;
        res.college = college;
        res.code = code;
        return res;
    }

    public Course get(){
        return ObjectSelect.query(Course.class)
                .where(Course.COLLEGE.eq(college))
                .and(Course.IS_WEB_VISIBLE.isTrue())
                .and(Course.CODE.eq(code))
                .cacheStrategy(LOCAL_CACHE)
                .cacheGroup(Course.class.getSimpleName())
                .selectOne(context);
    }
}
