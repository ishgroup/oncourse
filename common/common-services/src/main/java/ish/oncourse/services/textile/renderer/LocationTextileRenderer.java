package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.LocationTextileAttribute;
import ish.oncourse.services.textile.validator.LocationTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class LocationTextileRenderer extends AbstractRenderer {

    private IPageRenderer pageRenderer;

    public LocationTextileRenderer(IPageRenderer pageRenderer, ICayenneService cayenneService, IWebSiteService webSiteService) {
        this.pageRenderer = pageRenderer;
        validator = new LocationTextileValidator(cayenneService, webSiteService);
    }

    @Override
    protected String internalRender(String tag) {
        Map<String, String> tagParams = TextileUtil.getTagParams(tag,
                LocationTextileAttribute.getAttrValues());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TextileUtil.TEXTILE_LOCATION_PAGE_PARAM, tagParams);
        tag = pageRenderer.renderPage(TextileUtil.TEXTILE_LOCATION_PAGE,
                parameters);
        return tag;
    }
}