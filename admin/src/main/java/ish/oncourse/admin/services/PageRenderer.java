package ish.oncourse.admin.services;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.tapestry.IWillowComponentRequestSelectorAnalyzer;
import ish.oncourse.util.IComponentPageResponseRenderer;
import ish.oncourse.util.IPageRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.dom.MarkupModel;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import java.util.Map;

public class PageRenderer implements IPageRenderer {
    private static final Logger LOGGER = LogManager.getLogger(PageLoader.class);

    private PageLoader pageLoader;

    private RequestGlobals requestGlobals;

    private IComponentPageResponseRenderer pageResponseRenderer;
    private IWillowComponentRequestSelectorAnalyzer analyzer;

    @Inject
    public PageRenderer(PageLoader pageLoader, RequestGlobals requestGlobals,
                        IComponentPageResponseRenderer pageResponseRenderer,
                        IWillowComponentRequestSelectorAnalyzer analyzer) {
        this.pageLoader = pageLoader;
        this.requestGlobals = requestGlobals;
        this.pageResponseRenderer = pageResponseRenderer;
        this.analyzer = analyzer;
    }

    public String renderPage(String pageName, Map<String, Object> parameters) {
        Request request = requestGlobals.getRequest();
        request.setAttribute(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, null);
        for (String key : parameters.keySet()) {
            request.setAttribute(key, parameters.get(key));
        }

        Response response = requestGlobals.getResponse();
        GetStrResponseWrapper wrapper = new GetStrResponseWrapper(response);

        requestGlobals.storeRequestResponse(request, wrapper);

        ComponentResourceSelector selector = analyzer.buildSelectorForRequest(parameters);
        Page page = pageLoader.loadPage(pageName, selector);

        try {
            pageResponseRenderer.renderPageResponse(page);
        } catch (Exception e) {
            LOGGER.debug(e);
        }

        requestGlobals.storeRequestResponse(request, response);

        return wrapper.getResponseString();
    }

    public String encodedPage(String pageName, Map<String, Object> parameters) {
        String content = renderPage(pageName, parameters);
        StringBuilder builder = PageRenderer.encode(content);
        return builder.toString();
    }

    /**
     *Encodes the characters, converting control characters (such as '&lt;')
     * into corresponding entities (such as &amp;lt;). {@link MarkupModel}
     *
     * @param content
     * @return
     */
    public static StringBuilder encode(String content) {
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
