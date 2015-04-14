/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.Auth;
import io.milton.http.HttpManager;
import io.milton.http.Request;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.Resource;
import io.milton.servlet.Config;
import io.milton.servlet.DefaultMiltonConfigurator;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.access.AuthenticationStatus;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class Configurator extends DefaultMiltonConfigurator {

	@Override
	public HttpManager configure(Config config) throws ServletException {
		
		ServletContext servletContext = config.getServletContext();

		Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		builder.setMainResourceFactory(new RootResourceFactory(registry, 
						new CmsSecurityManager(registry.getService(IAuthenticationService.class))));
		
		builder.setEnableCookieAuth(false);
		builder.setEnabledJson(false);
		
		return super.configure(config);
	}
	
	private static class CmsSecurityManager implements io.milton.http.SecurityManager {
		
		private IAuthenticationService authenticationService;
		
		public CmsSecurityManager(IAuthenticationService authenticationService) {
			this.authenticationService = authenticationService;
		}
		
		@Override
		public Object authenticate(DigestResponse digestRequest) {
			throw new UnsupportedOperationException("Digest authentication is not supported.");
		}

		@Override
		public Object authenticate(String user, String password) {
			return AuthenticationStatus.SUCCESS.equals(authenticationService.authenticate(user, password)) ? user : null;
		}

		@Override
		public boolean authorise(Request request, Request.Method method, Auth auth, Resource resource) {
			// for now all authenticated users are authorised to perform any actions
			return auth != null;
		}

		@Override
		public String getRealm(String host) {
			return "oncourse";
		}

		@Override
		public boolean isDigestAllowed() {
			return false;
		}
	}
}
