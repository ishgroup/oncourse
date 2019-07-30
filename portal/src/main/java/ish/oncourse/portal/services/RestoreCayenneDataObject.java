package ish.oncourse.portal.services;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectIdQuery;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class RestoreCayenneDataObject {
    private ObjectContext objectContext;
    private Object candidate;
    private BaseDataObject result;

    public void restore() {
        ObjectIdQuery objectIdQuery = new ObjectIdQuery(((BaseDataObject) candidate).getObjectId());
        List list = objectContext.performQuery(objectIdQuery);
        if (list.size() == 1) {
            result = (BaseDataObject) list.get(0);
        } else {
            result = null;
        }
    }

    public boolean needToBeRestored() {
        return (candidate instanceof BaseDataObject && ((BaseDataObject) candidate).getObjectContext() == null);
    }


    public BaseDataObject getResult() {
        return result;
    }

    public static RestoreCayenneDataObject valueOf(Object candidate, ObjectContext objectContext) {
        RestoreCayenneDataObject restoreCayenneDataObject = new RestoreCayenneDataObject();
        restoreCayenneDataObject.candidate = candidate;
        restoreCayenneDataObject.objectContext = objectContext;
        return restoreCayenneDataObject;
    }
}
