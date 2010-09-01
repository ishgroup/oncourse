package ish.oncourse.util;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.internal.services.DocumentLinker;
import org.apache.tapestry5.internal.services.PageContentTypeAnalyzer;
import org.apache.tapestry5.internal.services.PageMarkupRenderer;
import org.apache.tapestry5.internal.services.PageResponseRendererImpl;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ClientBehaviorSupport;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Heartbeat;
import org.apache.tapestry5.services.MarkupWriterFactory;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class ComponentPageResponseRenderer implements IPageResponseRenderer {

	@Inject
	private PageMarkupRenderer markupRenderer;
	
	@Inject
	private MarkupWriterFactory markupWriterFactory;
	
	@Inject
	private PageContentTypeAnalyzer pageContentTypeAnalyzer;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private Environment environment;

	/**
	 * The same implementation of method as for {@see PageResponseRendererImpl}
	 * except of obtaining the response - we get it from requestGlobals
	 */
	public void renderPageResponse(Page page) throws IOException {
		assert page != null;

		ContentType contentType = pageContentTypeAnalyzer.findContentType(page);

		MarkupWriter writer = markupWriterFactory.newMarkupWriter(contentType);

		markupRenderer.renderPageMarkup(page, writer);

		PrintWriter pw = requestGlobals.getResponse().getPrintWriter(
				contentType.toString());
		writer.toMarkup(pw);

		pw.close();

		//TODO without this hack TapestryModule filters are trying to pop the empty stack
		environment.push(ValidationDecorator.class, null);
		environment.push(Heartbeat.class, null);
		environment.push(DocumentLinker.class, null);
		environment.push(JavaScriptSupport.class, null);
		environment.push(RenderSupport.class, null);
		environment.push(ClientBehaviorSupport.class, null);
	}

}
