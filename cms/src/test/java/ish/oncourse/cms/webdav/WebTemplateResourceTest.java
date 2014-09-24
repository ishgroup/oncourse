package ish.oncourse.cms.webdav;

import ish.oncourse.model.*;
import ish.oncourse.services.templates.IWebTemplateService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebTemplateResourceTest {

    @Test
    public void test_WebTemplateResource_getContentLength()
    {
        //the content has 17 bytes and 13 chars
        String utf8Content = "“HELLO WORLD”";

        WebTemplate webTemplate = mock(WebTemplate.class);
        when(webTemplate.getContent()).thenReturn(utf8Content);

        IWebTemplateService webTemplateService = mock(IWebTemplateService.class);
        when(webTemplateService.getTemplateByName(any(String.class), any(WebSiteLayout.class))).thenReturn(webTemplate);

        WebTemplateResource webTemplateResource = new WebTemplateResource("test.tml",
                null,
                null,
                webTemplateService,
                null, null);

        Assert.assertEquals("getContentLength() should return amount of bytes, not chars", (long) utf8Content.getBytes().length, (long) webTemplateResource.getContentLength());
    }

    @Test
    public void test_WebNodeResource_getContentLength()
    {
        //the content has 17 bytes and 13 chars
        String utf8Content = "“HELLO WORLD”";

        WebContent webContent = mock(WebContent.class);
        when(webContent.getContentTextile()).thenReturn(utf8Content);

        WebContentVisibility webContentVisibility = mock(WebContentVisibility.class);
        when(webContentVisibility.getWebContent()).thenReturn(webContent);

        WebNode webNode = mock(WebNode.class);
        when(webNode.getWebContentVisibility()).thenReturn(Collections.singletonList(webContentVisibility));

        WebNodeResource webNodeResource = new WebNodeResource(webNode,null, null, null, null);
        Assert.assertEquals("getContentLength() should return amount of bytes, not chars", (long) utf8Content.getBytes().length,
                (long) webNodeResource.getContentLength());

    }

    @Test
    public void test_WebContent_getContentLength()
    {
        //the content has 17 bytes and 13 chars
        String utf8Content = "“HELLO WORLD”";

        WebContent webContent = mock(WebContent.class);
        when(webContent.getContentTextile()).thenReturn(utf8Content);

        WebContentResource resource = new WebContentResource(webContent, null, null, null, null);
        Assert.assertEquals("getContentLength() should return amount of bytes, not chars", (long) utf8Content.getBytes().length,
                (long) resource.getContentLength());

    }
}
