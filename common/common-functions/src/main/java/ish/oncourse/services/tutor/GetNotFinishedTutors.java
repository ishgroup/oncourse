package ish.oncourse.services.tutor;

import ish.oncourse.model.College;
import ish.oncourse.model.Tutor;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetNotFinishedTutors {

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private College college;

    private GetNotFinishedTutors() {}

    public static GetNotFinishedTutors valueOf(ObjectContext context, QueryCacheStrategy strategy, College college) {
        GetNotFinishedTutors obj = new GetNotFinishedTutors();
        obj.context = context;
        obj.strategy = strategy;
        obj.college = college;
        return obj;
    }

    public List<Tutor> get() {
        ObjectSelect query = ObjectSelect.query(Tutor.class).where(getQualifier(college));
        if (strategy != null) {
            query = query.cacheStrategy(strategy);
        }
        return new ArrayList<>(query.select(context));
    }

    protected Expression getQualifier(College college) {
        return Tutor.COLLEGE.eq(college)
                .andExp(Tutor.FINISH_DATE.isNull()
                        .orExp(Tutor.FINISH_DATE.isNotNull().andExp(Tutor.FINISH_DATE.gt(new Date()))));
    }
}
