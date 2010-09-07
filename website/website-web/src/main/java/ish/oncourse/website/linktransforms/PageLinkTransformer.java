/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.website.linktransforms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.URLEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;


/**
 *
 * @author marek
 */
public class PageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger LOGGER = Logger.getLogger(PageLinkTransformer.class);
	private static final String PAGE_NODE_REGEX = "/page/(\\d++)";

	@Inject
	PageRenderLinkSource pageRenderLinkSource;


	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		final String path = request.getPath();

		LOGGER.info("Rewrite InBound: path is: " + path);

		if (path.startsWith("/page/")) {
			Pattern pattern = Pattern.compile(PAGE_NODE_REGEX);
			Matcher matcher = pattern.matcher(path);

			if (matcher.find()) {
				String node = matcher.group(1);
				if (node != null) {
					EventContext context = new ArrayEventContext(null, node);
					PageRenderRequestParameters newRequest = new PageRenderRequestParameters(
							"Page", context, false);
					LOGGER.info("Rewrite InBound: Matched page node! Path: '" + path + "', Node: '" + node + "'");

					return newRequest;
				}
			}
		}

		return null;
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
