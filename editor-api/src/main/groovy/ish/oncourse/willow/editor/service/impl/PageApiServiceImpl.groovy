package ish.oncourse.willow.editor.service.impl;

import ish.oncourse.willow.editor.service.*;
import ish.oncourse.willow.editor.model.Page;
import ish.oncourse.willow.editor.model.api.PageRenderResponse;
import ish.oncourse.willow.editor.model.common.CommonError;

import groovy.transform.CompileStatic

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@CompileStatic
public class PageApiServiceImpl implements PageApi {
    public CommonError addPage() {
        // TODO: Implement...
        
        return null;
    }
    
    public void deletePage(Double id) {
        // TODO: Implement...
        
        
    }
    
    public Page getPageByUrl(String pageUrl) {
        // TODO: Implement...
        
        return null;
    }
    
    public PageRenderResponse getPageRender(Double pageId) {
        // TODO: Implement...
        
        return null;
    }
    
    public List<Page> getPages() {
        // TODO: Implement...
        
        return null;
    }
    
    public Page savePage(Page pageParams) {
        // TODO: Implement...
        
        return null;
    }
    
}

