/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.portal.services;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.usi.Usi;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import java.util.regex.Matcher;

public class PageLinkTransformer implements PageRenderLinkTransformer {

    @Inject
    private ICayenneService cayenneService;


    @Override
    public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
        return defaultLink;
    }

    @Override
    public PageRenderRequestParameters decodePageRenderRequest(Request request) {
        String path = request.getPath();

        Matcher matcher = Usi.REGEXP_USI_PATH.matcher(path);
        if (matcher.matches()) {

            ObjectContext context = cayenneService.newNonReplicatingContext();
            Expression expression = ExpressionFactory.matchAllExp(Contact.GIVEN_NAME_PROPERTY, "James");
            expression = expression.andExp(ExpressionFactory.matchAllExp(Contact.FAMILY_NAME_PROPERTY, "Saum"));
            expression = expression.andExp(ExpressionFactory.matchAllExp(Contact.EMAIL_ADDRESS_PROPERTY, "JamesKSaum@dayrep.com"));

            SelectQuery query = new SelectQuery(Contact.class, expression);
            Contact contact = (Contact) context.performQuery(query).get(0);
            request.setAttribute(Usi.ATTR_usiContact, contact);
            return new PageRenderRequestParameters("usi/usi", new EmptyEventContext(), false);
        }

        return null;
    }
}
