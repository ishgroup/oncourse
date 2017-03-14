package ish.oncourse.services.course.functions;

import ish.oncourse.model.Course;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

/**
 * Cache settings for course query
 * Created by pavel on 3/13/17.
 */
public class ApplyCourseCacheSettings {

    private ObjectSelect objectSelect;

    private ApplyCourseCacheSettings () {}

    public static ApplyCourseCacheSettings valueOf(ObjectSelect query){
        ApplyCourseCacheSettings result = new ApplyCourseCacheSettings();
        result.objectSelect = query;
        return result;
    }

    public ObjectSelect apply(){
        objectSelect.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Course.class.getSimpleName());
        return objectSelect;
    }
}
