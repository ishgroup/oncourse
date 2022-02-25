/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.cayenne.InvoiceLine;
import ish.util.EntityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;

public class InvoiceLinePreCreateHandler implements GraphChangeHandler {
    private ObjectContext currentContext;

    public InvoiceLinePreCreateHandler(ObjectContext currentContext){
        this.currentContext = currentContext;
    }

    @Override
    public void nodeIdChanged(Object nodeId, Object newId) {
        System.out.println("temp");
    }

    @Override
    public void nodeCreated(Object nodeId) {
        if(nodeId instanceof ObjectId){
            ObjectId node = (ObjectId) nodeId;
           // if(node.getEntityName().equals(InvoiceLine.ENTITY_NAME)){
                System.out.println("temp");
           // }
        }
    }

    @Override
    public void nodeRemoved(Object nodeId) {
        System.out.println("temp");
    }

    @Override
    public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
        System.out.println("temp");
    }

    @Override
    public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {
        System.out.println("temp");
    }

    @Override
    public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {
        System.out.println("temp");
    }
}
