package ish.oncourse.portal.services.pageload;

import ish.oncourse.util.GetStrResponseWrapper;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import org.apache.tapestry5.dom.MarkupModel;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.pageload.ComponentRequestSelectorAnalyzer;

import java.util.Map;

public class PortalPageRenderer implements IPageRenderer {
	
	@Inject
	private PageLoader pageLoader;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private IComponentPageResponseRenderer pageResponseRenderer;
	
	@Inject
	private ComponentRequestSelectorAnalyzer selectorAnalyzer;

	public String renderPage(String pageName, Map<String, Object> parameters) {
		Request request = requestGlobals.getRequest();
		for (String key : parameters.keySet()) {
			request.setAttribute(key, parameters.get(key));
		}

		Response response = requestGlobals.getResponse();
		GetStrResponseWrapper wrapper = new GetStrResponseWrapper(response);

		requestGlobals.storeRequestResponse(request, wrapper);
		Page page = pageLoader.loadPage(pageName, selectorAnalyzer.buildSelectorForRequest());

		try {
			pageResponseRenderer.renderPageResponse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}

		requestGlobals.storeRequestResponse(request, response);

		return wrapper.getResponseString();
	}

	public String encodedPage(String pageName, Map<String, Object> parameters) {
		String content = renderPage(pageName, parameters);
		StringBuilder builder = encode(content);
		return builder.toString();
	}

	/**
	 *Encodes the characters, converting control characters (such as '&lt;')
	 * into corresponding entities (such as &amp;lt;). {@link MarkupModel}
	 * 
	 * @param content
	 * @return
	 */
	private StringBuilder encode(String content) {
		assert content != null;
		int length = content.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char ch = content.charAt(i);

			switch (ch) {
			case '<':

				builder.append("&lt;");
				continue;

			case '>':

				builder.append("&gt;");
				continue;

			case '&':

				builder.append("&amp;");
				continue;

			case '"':

				builder.append("&quot;");
				continue;

			case '\'':

				builder.append("&#39;");

				// Fall through

			default:

				builder.append(ch);
			}
		}
		return builder;
	}

}
