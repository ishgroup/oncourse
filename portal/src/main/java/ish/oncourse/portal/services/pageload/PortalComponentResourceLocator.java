package ish.oncourse.portal.services.pageload;

import java.util.List;

import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
@Deprecated
public class PortalComponentResourceLocator implements ComponentResourceLocator {
	
	private static final String MOBILE_FOLDER = "mobile/";
	
	private static final String DESKTOP_FOLDER = "desktop/";

	private final ComponentResourceLocator delegate;

	public PortalComponentResourceLocator(ComponentResourceLocator delegate) {
		super();
		this.delegate = delegate;
	}

	public List<Resource> locateMessageCatalog(Resource baseResource, ComponentResourceSelector selector) {
		return delegate.locateMessageCatalog(baseResource, selector);
	}

	public Resource locateTemplate(ComponentModel model, ComponentResourceSelector selector) {

		UserAgent userAgent = selector.getAxis(UserAgent.class);
	
		Resource baseResource = model.getBaseResource();
		
		String path = (userAgent.isMobile() ? MOBILE_FOLDER : DESKTOP_FOLDER) + baseResource.getPath().replace(".class", String.format(".%s", TapestryConstants.TEMPLATE_EXTENSION));

		Resource resource = new ClasspathResource(path);

		return resource.exists() ? resource.forLocale(selector.locale) : delegate.locateTemplate(model, selector);
	}
}
