/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.ContentTypeUtils;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.GetableResource;
import io.milton.resource.PropFindableResource;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.alias.IWebUrlAliasService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RedirectsResource extends AbstractResource implements GetableResource,PropFindableResource {

    public static final String FILE_NAME = "redirects.txt";

    private IWebUrlAliasService webUrlAliasService;

    public RedirectsResource(io.milton.http.SecurityManager securityManager, IWebUrlAliasService webUrlAliasService) {
        super(securityManager);
        this.webUrlAliasService = webUrlAliasService;
    }

    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
        List<WebUrlAlias> urlAliases = webUrlAliasService.getRedirects();

        for (WebUrlAlias urlAlias : urlAliases) {
            out.write(String.format("%s\t%s\n", urlAlias.getUrlPath(), urlAlias.getRedirectTo()).getBytes());
        }
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    @Override
    public String getContentType(String accepts) {
        return ContentTypeUtils.findAcceptableContentType("text", accepts);
    }

    @Override
    public Long getContentLength() {
        List<WebUrlAlias> urlAliases = webUrlAliasService.getRedirects();

        StringBuilder value = new StringBuilder();

        for (WebUrlAlias urlAlias : urlAliases) {
            value.append(String.format("%s\t%s\n", urlAlias.getUrlPath(), urlAlias.getRedirectTo()));
        }

        return (long)value.toString().getBytes().length;
    }

    @Override
    public String getUniqueId() {
        return null;
    }

    @Override
    public String getName() {
        return FILE_NAME;
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

    @Override
    public Date getCreateDate() {
        return new Date();
    }
}
