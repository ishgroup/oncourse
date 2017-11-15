package ish.oncourse.willow.editor.webdav

import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebTemplate
import org.junit.Assert
import org.junit.Test

class WebTemplateResourceTest {
    
    //the content has 17 bytes and 13 chars
    private String utf8Content = '“HELLO WORLD”'

    @Test
    void test_WebTemplateResource_getContentLength() {
        //the content has 17 bytes and 13 chars
        WebTemplate webTemplate = [getContent : utf8Content] as WebTemplate
        WebTemplateResource webTemplateResource = new WebTemplateResource('name.tml', null, null, null, null, { n, l -> webTemplate } )
        Assert.assertEquals('getContentLength() should return amount of bytes, not chars', utf8Content.bytes.length as long, webTemplateResource.contentLength as long)
    }

    @Test
    void test_WebNodeResource_getContentLength() {

        WebContent webContent = [getContentTextile : utf8Content] as WebContent
        WebContentVisibility webContentVisibility = [getWebContent : webContent] as WebContentVisibility
        WebNode webNode =  [getWebContentVisibility : [webContentVisibility]] as WebNode

        WebNodeResource webNodeResource = new WebNodeResource(webNode,null, null, null)
        Assert.assertEquals('getContentLength() should return amount of bytes, not chars', utf8Content.bytes.length as long,
                webNodeResource.contentLength as long)
    }

    @Test
    void test_WebContent_getContentLength() {
        
        WebContent webContent = [getContentTextile : utf8Content] as WebContent
        
        WebContentResource resource = new WebContentResource(webContent, null, null, null)
        Assert.assertEquals('getContentLength() should return amount of bytes, not chars', utf8Content.bytes.length as long,
                resource.contentLength as long)

    }
}
