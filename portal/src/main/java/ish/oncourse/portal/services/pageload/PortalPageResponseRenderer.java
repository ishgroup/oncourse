package ish.oncourse.portal.services.pageload;

import ish.oncourse.util.IComponentPageResponseRenderer;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.services.PageContentTypeAnalyzer;
import org.apache.tapestry5.internal.services.RenderQueueImpl;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.MarkupWriterFactory;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public class PortalPageResponseRenderer implements IComponentPageResponseRenderer {

	@Inject
	private MarkupWriterFactory markupWriterFactory;

	@Inject
	private PageContentTypeAnalyzer pageContentTypeAnalyzer;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Environment environment;

	@Inject
	private LoggerSource loggerSource;

	/**
	 * The same implementation of method as for {@see PageResponseRendererImpl}
	 * except of obtaining the response - we get it from requestGlobals
	 */
	public void renderPageResponse(Page page) throws IOException {
		assert page != null;

//		Heartbeat heartbeat = environment.peek(Heartbeat.class);
//		DocumentLinker documentLinker = environment.peek(DocumentLinker.class);
//		JavaScriptSupport javaScriptSupport = environment.peek(JavaScriptSupport.class);

		ContentType contentType = pageContentTypeAnalyzer.findContentType(page);

		MarkupWriter writer = markupWriterFactory.newMarkupWriter(page);

		String name = "tapestry.render." + page.getLogger().getName();
		Logger logger = loggerSource.getLogger(name);

		RenderQueueImpl renderQueue = new RenderQueueImpl(logger);
		renderQueue.push(page.getRootElement());
		renderQueue.run(writer);

		PrintWriter pw = requestGlobals.getResponse().getPrintWriter(
				contentType.toString());
		writer.toMarkup(pw);

		pw.close();
	}

}
