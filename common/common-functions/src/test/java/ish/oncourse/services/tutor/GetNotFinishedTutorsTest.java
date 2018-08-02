package ish.oncourse.services.tutor;

import ish.oncourse.model.College;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class GetNotFinishedTutorsTest {

    @Test
    public void testExpression() {
        ObjectContext context = mock(ObjectContext.class);
        College college = mock(College.class);
        GetNotFinishedTutors.valueOf(context, null, college).get();
        Mockito.verify(context,
                data -> {
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toString().startsWith("(college = null) and ((finishDate = null) or (not ((finishDate = null)) and (finishDate > "));
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toString().endsWith(")))"));
                }).selectOne(Mockito.any(Select.class));
    }
}
