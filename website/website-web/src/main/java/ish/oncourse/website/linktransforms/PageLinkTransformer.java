/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.website.linktransforms;

import ish.oncourse.services.node.IWebNodeService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
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
	private static final Pattern REGEX_NODE_PATTERN = Pattern.compile("/page/(\\d++)");
	private static final Pattern COURSES_PATTERN = Pattern.compile("/courses(/(\\w++))?");
	private static final Pattern PAGE_PATTERN = Pattern.compile("/page(/(\\w++))?");

	@Inject
	PageRenderLinkSource pageRenderLinkSource;

	@Inject
	TypeCoercer typeCoercer;


	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		final String path = request.getPath();

		LOGGER.info("Rewrite InBound: path is: " + path);
		Matcher matcher = REGEX_NODE_PATTERN.matcher(path);

		if (matcher.find()) {
			String nodeNumber = matcher.group(1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
				PageRenderRequestParameters newRequest = new PageRenderRequestParameters(
						"ui/Page", new EmptyEventContext(), false);
				LOGGER.info("Rewrite InBound: Matched page node! Path: '" + path + "', Node: '" + nodeNumber + "'");

				return newRequest;
			}
		}
		
		if(COURSES_PATTERN.matcher(path).find()){
			PageRenderRequestParameters newRequest = new PageRenderRequestParameters(
					"ui/Courses", new EmptyEventContext(), false);
			
			return newRequest;
		}

		if(PAGE_PATTERN.matcher(path).find()){
			PageRenderRequestParameters newRequest = new PageRenderRequestParameters(
					"ui/Page", new EmptyEventContext(), false);
			
			return newRequest;
		}
		return null;
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
