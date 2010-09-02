package ish.oncourse.util;

import java.util.Map;

import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

public class PageRenderer implements IPageRenderer{
	
	@Inject
	private RequestPageCache cache;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private IComponentPageResponseRenderer pageResponseRenderer;
	
	public String renderPage(String pageName, Map<String, Object> parameters) {
		Request request = requestGlobals.getRequest();
		for(String key:parameters.keySet()){
			request.setAttribute(key, parameters.get(key));
		}
		
		Response response = requestGlobals.getResponse();
		GetStrResponseWrapper wrapper = new GetStrResponseWrapper(
				response);

		requestGlobals.storeRequestResponse(request, wrapper);
		Page page = cache.get(pageName);

		try {
			pageResponseRenderer.renderPageResponse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}

		requestGlobals.storeRequestResponse(request, response);

		return wrapper.getResponseString();
	}

}
