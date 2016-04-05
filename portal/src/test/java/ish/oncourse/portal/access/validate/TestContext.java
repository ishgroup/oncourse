package ish.oncourse.portal.access.validate;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Document;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Select;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by akoiro on 5/04/2016.
 */
public class TestContext {

    private IPortalService portalService;

    private IBinaryDataService binaryDataService;

    private ICayenneService cayenneService;

    private ObjectContext objectContext;

    private List<Document> documents;
    private CourseClass courseClass;


    public void init() {
        portalService = mock(IPortalService.class);
        binaryDataService = mock(IBinaryDataService.class);
        courseClass = mock(CourseClass.class);
        objectContext = mock(ObjectContext.class);
        cayenneService = mock(ICayenneService.class);

        Document document1 = mock(Document.class);
        Document document2 = mock(Document.class);
        documents = new ArrayList<>();
        documents.add(document1);
        documents.add(document2);


        when(cayenneService.newContext()).thenReturn(objectContext);
        when(objectContext.selectOne(Mockito.any(Select.class))).thenReturn(courseClass);
        when(portalService.getResourcesBy(courseClass)).thenReturn(documents);
        when(binaryDataService.getUrl(document1)).thenReturn("https://www.skillsoncourse.com.au/portal/resource/716b5fc5-f435-4af8-a926-3f3d4b0ebb13");
        when(binaryDataService.getUrl(document2)).thenReturn("https://www.skillsoncourse.com.au/portal/resource/e934628d-8c87-4aeb-a50d-4e00f2df6d3a");
    }

    public IPortalService getPortalService() {
        return portalService;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public IBinaryDataService getBinaryDataService() {
        return binaryDataService;
    }

    public ICayenneService getCayenneService() {
        return cayenneService;
    }

    public CourseClass getCourseClass() {
        return courseClass;
    }
}
