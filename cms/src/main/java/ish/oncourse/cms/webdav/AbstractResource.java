/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.resource.Resource;

public abstract class AbstractResource implements Resource {
	
	private SecurityManager securityManager;
	
	public AbstractResource(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public Object authenticate(String user, String requestedPassword) {
		return securityManager.authenticate(user, requestedPassword);
	}

	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	@Override
	public boolean authorise(Request request, Request.Method method, Auth auth) {
		return securityManager.authorise(request, method, auth, this);
	}

	@Override
	public String getRealm() {
		return securityManager.getRealm(null);
	}
}
