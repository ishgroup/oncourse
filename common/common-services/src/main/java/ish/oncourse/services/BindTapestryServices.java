/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services;

import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.content.cache.IContentCacheService;
import ish.oncourse.services.content.cache.IContentKeyFactory;
import ish.oncourse.services.content.cache.WillowContentKeyFactory;
import ish.oncourse.services.cookies.CookiesImplOverride;
import ish.oncourse.services.cookies.CookiesService;
import ish.oncourse.services.cookies.ICookiesOverride;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.datalayer.DataLayerFactory;
import ish.oncourse.services.datalayer.IDataLayerFactory;
import ish.oncourse.services.format.FormatService;
import ish.oncourse.services.format.IFormatService;
import ish.oncourse.services.html.*;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.PropertyService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;

/**
 * User: akoiro
 * Date: 14/12/17
 */
public class BindTapestryServices {
	private Class<? extends IPageRenderer> pageRendererClass = PageRenderer.class;
	private Class<? extends ICacheMetaProvider> cacheMetaProviderClass = NoCacheMetaProvider.class;
	private ServiceBuilder<IResourceService> resourceService = null;


	public void bind(ServiceBinder binder) {
		binder.bind(ICookiesService.class, CookiesService.class);
		binder.bind(ICookiesOverride.class, CookiesImplOverride.class);
		binder.bind(IFormatService.class, FormatService.class);
		binder.bind(IPageRenderer.class, pageRendererClass);
		binder.bind(IPropertyService.class, PropertyService.class);
		if (resourceService == null)
			binder.bind(IResourceService.class, ResourceService.class);
		else
			binder.bind(IResourceService.class, resourceService);
		binder.bind(ITextileConverter.class, TextileConverter.class);
		binder.bind(IParsedContentVisitor.class, ParsedContentVisitor.class);
		binder.bind(IPlainTextExtractor.class, JerichoPlainTextExtractor.class);
		binder.bind(IDataLayerFactory.class, DataLayerFactory.class).scope(ScopeConstants.PERTHREAD);
		binder.bind(IFacebookMetaProvider.class, FacebookMetaProvider.class);
		binder.bind(IContentCacheService.class, new BinderFunctions.ContentCacheServiceBuilder());
		binder.bind(IContentKeyFactory.class, WillowContentKeyFactory.class).scope(ScopeConstants.PERTHREAD);
		binder.bind(IRequestCacheService.class, RequestCacheService.class);
		binder.bind(ICacheMetaProvider.class, cacheMetaProviderClass).eagerLoad();
		binder.bind(IComponentPageResponseRenderer.class, ComponentPageResponseRenderer.class);

	}

	public BindTapestryServices pageRendererClass(Class<? extends IPageRenderer> pageRendererClass) {
		this.pageRendererClass = pageRendererClass;
		return this;
	}

	public BindTapestryServices cacheMetaProviderClass(Class<? extends ICacheMetaProvider> cacheMetaProviderClass) {
		this.cacheMetaProviderClass = cacheMetaProviderClass;
		return this;
	}
}
