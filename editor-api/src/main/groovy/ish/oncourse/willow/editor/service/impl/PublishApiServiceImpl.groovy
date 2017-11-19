package ish.oncourse.willow.editor.service.impl

import groovy.transform.CompileStatic;
import ish.oncourse.willow.editor.service.*;
import ish.oncourse.willow.editor.model.Version;
import ish.oncourse.willow.editor.model.api.SetVersionRequest;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@CompileStatic
public class PublishApiServiceImpl implements PublishApi {
    public List<Version> getVersions() {
        // TODO: Implement...
        
        return null;
    }
    
    public void publish() {
        // TODO: Implement...
        
        
    }
    
    public void setVersion(SetVersionRequest setVersionRequest) {
        // TODO: Implement...
        
        
    }
    
}

