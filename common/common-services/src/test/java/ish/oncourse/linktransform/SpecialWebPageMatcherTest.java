package ish.oncourse.linktransform;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Select;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class SpecialWebPageMatcherTest {

    @Test
    public void test() {
        ObjectContext context = mock(ObjectContext.class);
        SpecialWebPageMatcher.valueOf(context, null, mock(WebSite.class), "/requestPath").get();

        Mockito.verify(context,
                data -> {
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(0).getRawArguments()[0]).getWhere().toEJBQL("site").toString().equals("site.webSite is null"));
                    Assert.assertTrue(((ObjectSelect) data.getAllInvocations().get(1).getRawArguments()[0]).getWhere().toEJBQL("site").toString().equals("(site.webSiteVersion is null) and (site.matchType = enum:ish.common.specialpages.RequestMatchType.EXACT) and (upper(site.urlPath) like '/REQUESTPATH')"));
                }).selectOne(Mockito.any(Select.class));
    }
}
