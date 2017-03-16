package ish.oncourse.services;

import ish.oncourse.util.ComponentPageResponseRenderer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.PageRenderer;
import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * Created by anarut on 3/14/17.
 */
public class SimpleTapestryTestModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(IComponentPageResponseRenderer.class, ComponentPageResponseRenderer.class);
		binder.bind(IPageRenderer.class, PageRenderer.class);
	}
}
