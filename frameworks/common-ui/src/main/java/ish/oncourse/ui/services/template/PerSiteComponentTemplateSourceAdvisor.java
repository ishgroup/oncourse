package ish.oncourse.ui.services.template;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Invocation;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.ComponentModel;

import ish.oncourse.ui.components.PageWrapper;

import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.site.IWebSiteService;

public class PerSiteComponentTemplateSourceAdvisor implements
		IComponentTemplateSourceAdvisor {

	@Inject
	private transient ICacheService cacheService;

	@Inject
	private transient TemplateParser templateParser;

	@Inject
	private transient IWebSiteService webSiteService;

	@Inject
	private transient IResourceService resourceService;

	private String overridablePackage;

	private String overridablePath;

	public PerSiteComponentTemplateSourceAdvisor() {
		String sampleComponentClass = PageWrapper.class.getName();
		overridablePackage = sampleComponentClass.substring(0,
				sampleComponentClass.lastIndexOf('.') + 1);
		overridablePath = overridablePackage.replace('.', '/');
	}

	public void advice(MethodAdviceReceiver receiver) {

		final String methodName = "getTemplate";

		Method method;
		try {
			method = receiver.getInterface().getMethod(methodName,
					ComponentModel.class, Locale.class);
		} catch (Exception e) {
			String message = String.format(
					"Can't find method '%s' in the interface '%s'", methodName,
					receiver.getInterface().getName());
			throw new RuntimeException(message, e);
		}

		receiver.adviseMethod(method, new MethodAdvice() {

			public void advise(Invocation invocation) {

				ComponentModel model = (ComponentModel) invocation
						.getParameter(0);

				ComponentTemplate template = overriddenTemplate(model);

				if (template != null) {
					invocation.overrideResult(template);
				} else {
					invocation.proceed();
				}
			}
		});
	}

	private ComponentTemplate overriddenTemplate(
			final ComponentModel componentModel) {

		String componentName = componentModel.getComponentClassName();
		if (!componentName.startsWith(overridablePackage)) {
			return null;
		}

		String key = createTemplateKey(componentName);

		return cacheService.get(key,
				new CachedObjectProvider<ComponentTemplate>() {
					public ComponentTemplate create() {
						return createOverriddenTemplate(componentModel);
					}
				}, CacheGroup.TEMPLATE_OVERRIDE);
	}

	private ComponentTemplate createOverriddenTemplate(
			ComponentModel componentModel) {

		Resource templateResource = locateTemplateResource(componentModel);

		if (templateResource != null) {
			return templateParser.parseTemplate(templateResource);
		}

		return null;
	}

	private Resource locateTemplateResource(ComponentModel model) {

		// for now ignoring the Locale...

		Resource t5BaseResource = model.getBaseResource().withExtension(
				InternalConstants.TEMPLATE_EXTENSION);

		String path = t5BaseResource.getPath();
		if (path.startsWith(overridablePath)) {
			PrivateResource resource = resourceService.getT5Resource(path
					.substring(overridablePath.length()));

			// extract the resource file on the spot, to (1) check whether it
			// exists and (2) to avoid indeterministic behavior later on when
			// the resource is used, as a PrivateResource can point to a group
			// files, and exact file can change on disk over the lifespan of the
			// resource, creating a race condition

			File file;
			try {
				file = resource.getFile();
			} catch (IllegalStateException e) {
				return null;
			}

			return new T5FileResource(file);
		}

		return null;
	}

	private String createTemplateKey(String componentName) {
		return PerSiteComponentTemplateSourceAdvisor.class.getSimpleName() + ":"
				+ webSiteService.getCurrentSite().getCode() + "@"
				+ componentName;
	}
}
