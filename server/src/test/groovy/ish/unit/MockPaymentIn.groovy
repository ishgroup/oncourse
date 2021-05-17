/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.unit

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.PaymentIn
import org.apache.cayenne.DataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.Persistent

@CompileStatic
class MockPaymentIn extends PaymentIn {

    private int id

    MockPaymentIn(int id, ObjectContext objectContext) {
        this.id = id
        setObjectContext(objectContext)
    }

    /**
     * @see org.apache.cayenne.CayenneDataObject#getObjectId()
     */
    @Override
    ObjectId getObjectId() {
        return new ObjectId("PaymentIn", "id", this.id)
    }

    @Override
    protected void setReverseRelationship(String relName, DataObject val) {
        // do nothing
    }

    @Override
    protected void willConnect(String relationshipName, Persistent object) {
        // do nothing
    }
}