package ish.oncourse.portal.services;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.internal.services.SessionLock;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.SessionPersistedObjectAnalyzer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ISHClusteredSessionImpl extends SessionImpl {
	private final SessionPersistedObjectAnalyzer analyzer;


	private ICayenneService cayenneService;
	private final Map<String, Object> sessionAttributeCache = CollectionFactory.newMap();

	public ISHClusteredSessionImpl(
			HttpServletRequest request,
			HttpSession session,
			SessionLock sessionLock,
			SessionPersistedObjectAnalyzer analyzer,
			ICayenneService cayenneService) {
		super(request, session, sessionLock);
		this.analyzer = analyzer;
		this.cayenneService = cayenneService;
	}

	@Override
	public Object getAttribute(String name) {
		Object result = super.getAttribute(name);

		RestoreCayenneDataObject rcdo = RestoreCayenneDataObject.valueOf(result, cayenneService.sharedContext());
		if (rcdo.needToBeRestored()) {
			rcdo.restore();
			result = rcdo.getResult();
		}
		sessionAttributeCache.put(name, result);

		return result;
	}

	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);

		sessionAttributeCache.put(name, value);
	}

	public void invalidate() {
		super.invalidate();

		sessionAttributeCache.clear();
	}

	public void restoreDirtyObjects() {
		if (isInvalidated()) return;

		if (sessionAttributeCache.isEmpty()) return;

		for (Map.Entry<String, Object> entry : sessionAttributeCache.entrySet()) {
			String attributeName = entry.getKey();

			Object attributeValue = entry.getValue();

			if (attributeValue == null) continue;

			RestoreCayenneDataObject rcdo = RestoreCayenneDataObject.valueOf(attributeValue, cayenneService.sharedContext());
			if (rcdo.needToBeRestored()) {
				rcdo.restore();
				attributeValue = rcdo.getResult();
			}

			if (analyzer.checkAndResetDirtyState(attributeValue)) {
				super.setAttribute(attributeName, attributeValue);
			}
		}
	}
}
