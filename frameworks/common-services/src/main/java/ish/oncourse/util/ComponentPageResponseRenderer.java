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
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ClientBehaviorSupport;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Heartbeat;
import org.apache.tapestry5.services.MarkupWriterFactory;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class ComponentPageResponseRenderer implements IComponentPageResponseRenderer {

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
		ValidationDecorator validationDecorator=environment.peekRequired(ValidationDecorator.class);
		Heartbeat heartbeat = environment.peekRequired(Heartbeat.class);
		DocumentLinker documentLinker = environment.peekRequired(DocumentLinker.class);
		JavaScriptSupport javaScriptSupport = environment.peekRequired(JavaScriptSupport.class);
		RenderSupport renderSupport = environment.peekRequired(RenderSupport.class);
		ClientBehaviorSupport clientBehaviorSupport = environment.peekRequired(ClientBehaviorSupport.class);
		
		ContentType contentType = pageContentTypeAnalyzer.findContentType(page);

		MarkupWriter writer = markupWriterFactory.newMarkupWriter(contentType);

		markupRenderer.renderPageMarkup(page, writer);

		PrintWriter pw = requestGlobals.getResponse().getPrintWriter(
				contentType.toString());
		writer.toMarkup(pw);

		pw.close();

		//TODO without this hack TapestryModule filters are trying to pop the empty stack
		environment.push(ValidationDecorator.class, validationDecorator);
		environment.push(Heartbeat.class, heartbeat);
		environment.push(DocumentLinker.class, documentLinker);
		environment.push(JavaScriptSupport.class, javaScriptSupport);
		environment.push(RenderSupport.class, renderSupport);
		environment.push(ClientBehaviorSupport.class, clientBehaviorSupport);
	}

}
