package ish.oncourse.portal.services;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.internal.services.TapestrySessionFactoryImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.services.SessionPersistedObjectAnalyzer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ISHTapestrySessionFactoryImpl extends TapestrySessionFactoryImpl {
    private HttpServletRequest request;
    private boolean clustered;
    private SessionPersistedObjectAnalyzer analyzer;

    @Inject
    private ICayenneService cayenneService;

    public ISHTapestrySessionFactoryImpl(@Symbol(SymbolConstants.CLUSTERED_SESSIONS)
                                         boolean clustered, SessionPersistedObjectAnalyzer analyzer, HttpServletRequest request) {
        super(clustered, analyzer, request);
        this.request = request;
        this.analyzer = analyzer;
        this.clustered = clustered;
    }

    @Override
    public Session getSession(boolean create) {
        final HttpSession httpSession = request.getSession(create);

        if (httpSession == null) {
            return null;
        }

        if (clustered) {
            return new ISHClusteredSessionImpl(request, httpSession, analyzer, cayenneService);
        }

        return new SessionImpl(request, httpSession);
    }
}
