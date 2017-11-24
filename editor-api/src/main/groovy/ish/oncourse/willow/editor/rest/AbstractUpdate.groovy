package ish.oncourse.willow.editor.rest

import org.apache.cayenne.ObjectContext
import org.eclipse.jetty.server.Request

abstract class AbstractUpdate<T> {
    
    protected T resourceToSave
    protected ObjectContext context
    protected Request request

    protected String error = null

    
    String getError() {
        return error
    }

    void init(T resourceToSave, ObjectContext context, Request request) {
        this.resourceToSave = resourceToSave
        this.context = context
        this.request = request
    }
    
    abstract AbstractUpdate update()
}
