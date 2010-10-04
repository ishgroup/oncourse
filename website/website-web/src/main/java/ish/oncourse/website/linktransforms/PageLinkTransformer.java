/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.linktransforms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.WebNodeService;
import ish.oncourse.services.tag.ITagService;

public class PageLinkTransformer implements PageRenderLinkTransformer {

	private static final Logger LOGGER = Logger.getLogger(PageLinkTransformer.class);

	/**
	 * courses/arts/drama Show course list page, optionally filtered by the subject tag identified by arts -> drama
	 */
	private static final Pattern COURSES_PATTERN = Pattern.compile("/courses(/(.+)+)*");

	/**
	 * course/ABC Show course detail for the cource with code ABC
	 */
	private static final Pattern COURSE_PATTERN = Pattern.compile("/course/((\\w|\\s)+)");

	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	private static final Pattern CLASS_PATTERN = Pattern.compile("/class/((\\w|\\s)+)-((\\w|\\s)+)");

	/**
	 * page/123 This is always available for every webpage, even if it doesn't have a URL alias
	 */
	private static final Pattern PAGENUM_PATTERN = Pattern.compile("/page/(\\d+)");

	/**
	 * sites Show the site list for all sites
	 */
	private static final Pattern SITES_PATTERN = Pattern.compile("/sites");

	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	private static final Pattern SITE_PATTERN = Pattern.compile("/site/(\\d+)");
	
	/**
	 * room/200 Show the room detail for the room with angel id of 200
	 */
	private static final Pattern ROOM_PATTERN = Pattern.compile("/room/(\\d+)");

	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	private static final Pattern TUTOR_PATTERN = Pattern.compile("/tutor/(\\d+)");

	/**
	 * /sitemap.xml Google specific sitemap file.
	 */
	private static final Pattern SITEMAP_PATTERN = Pattern.compile("/sitemap\\.xml");

	private static final String HOME_PAGE_PATH = "/";

	/**
	 * Path of the search autocomplete
	 */
	private static final String ADVANCED_KEYWORD_PATH = "/advanced/keyword";
	
	/**
	 * Path of the suburbs autocomplete
	 */
	private static final String ADVANCED_SUBURB_PATH = "/advanced/suburbs";

	@Inject
	PageRenderLinkSource pageRenderLinkSource;

	@Inject
	TypeCoercer typeCoercer;

	@Inject
	RequestGlobals requestGlobals;
	
	@Inject
	IWebNodeService webNodeService;

	@Inject
	ITagService tagService;
	
	public PageRenderRequestParameters decodePageRenderRequest(Request request) {
		final String path = request.getPath().toLowerCase();

		// TODO remove the next test since it will be replaced with separate application
		if (path.startsWith("/assets") || path.startsWith("/servlet")) {
			return null;
		}

		if (HOME_PAGE_PATH.equals(path)) {
			return null;
		}

		Matcher matcher;

		LOGGER.info("Rewrite InBound: path is: " + path);

		/*
		 * For speed these are currently ordered from most likely to be encountered, to least.
		 */

		matcher = COURSES_PATTERN.matcher(path);
		if (matcher.matches()) {
			String tagsPath= path.replaceFirst("/courses", "");
			if(tagsPath.startsWith("/")){
				tagsPath=tagsPath.replaceFirst("/", "");
			}
			if(!tagsPath.equals("")){
				if(tagsPath.endsWith("/")){
					tagsPath=tagsPath.substring(0, tagsPath.length()-1);
				}
				String tags[]=tagsPath.split("/");
				Tag rootTag=tagService.getRootTag();
				for(String tag:tags){
					tag=tag.replaceAll("[+]", " ").replaceAll("[|]", "/");
					if(rootTag.hasChildWithName(tag)){
						rootTag=tagService.getSubTagByName(tag);
					}else{
						throw new NotImplementedException("URL alias");
					}
				}
				request.setAttribute(Course.COURSE_TAG, rootTag);
			}
			return new PageRenderRequestParameters("ui/Courses", new EmptyEventContext(), false);
		}

		matcher = COURSE_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseCode", courseCode);
			return new PageRenderRequestParameters("ui/CourseDetails", new EmptyEventContext(), false);
		}

		matcher = CLASS_PATTERN.matcher(path);
		if (matcher.matches()) {
			String courseClassCode = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("courseClassCode", courseClassCode);
			return new PageRenderRequestParameters("ui/CourseClassDetails", new EmptyEventContext(), false);
		}

		matcher = PAGENUM_PATTERN.matcher(path);
		if (matcher.matches()) {
			String nodeNumber = matcher.group(1);
			if (nodeNumber != null) {
				request.setAttribute(IWebNodeService.NODE_NUMBER_PARAMETER, nodeNumber);
				return new PageRenderRequestParameters("ui/Page", new EmptyEventContext(), false);
			}
		}

		matcher = SITES_PATTERN.matcher(path);
		if (matcher.matches()) {
			return new PageRenderRequestParameters("ui/Sites", new EmptyEventContext(), false);
		}

		matcher = SITE_PATTERN.matcher(path);
		if (matcher.matches()) {
			String siteId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("siteId", siteId);
			return new PageRenderRequestParameters("ui/SiteDetails", new EmptyEventContext(), false);
		}
		
		matcher = ROOM_PATTERN.matcher(path);
		if (matcher.matches()) {
			String roomId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("roomId", roomId);
			return new PageRenderRequestParameters("ui/RoomDetails", new EmptyEventContext(), false);
		}

		matcher = TUTOR_PATTERN.matcher(path);
		if (matcher.matches()) {
			String tutorId = path.substring(path.lastIndexOf("/") + 1);
			request.setAttribute("tutorId", tutorId);
			return new PageRenderRequestParameters("ui/TutorDetails", new EmptyEventContext(), false);
		}

		matcher = SITEMAP_PATTERN.matcher(path);
		if (matcher.matches()) {
			return new PageRenderRequestParameters("ui/SitemapXML", new EmptyEventContext(), false);
		}
		String nodePath=path;
		if(nodePath.startsWith("/")){
			nodePath=nodePath.replaceFirst("/", "");
		}
		if(nodePath.endsWith("/")){
			nodePath=nodePath.substring(0, nodePath.length()-1);
		}
		if(webNodeService.isNodeExist(nodePath)){
			request.setAttribute(WebNodeService.PAGE_PATH_PARAMETER, path);
			return new PageRenderRequestParameters("ui/Page", new EmptyEventContext(), false);
		}
		if(ADVANCED_KEYWORD_PATH.equals(path)){
			return new PageRenderRequestParameters("ui/QuickSearchView", new EmptyEventContext(), false);
		}
		if(ADVANCED_SUBURB_PATH.equals(path)){
			return new PageRenderRequestParameters("ui/SuburbsTextArray", new EmptyEventContext(), false);
		}
		// If we match no other pattern we need to look up the page in the list
		// of URL aliases
		throw new NotImplementedException("URL alias");
	}

	public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
		LOGGER.info("Rewrite OutBound: path is: " + defaultLink.getBasePath());

		return defaultLink;
	}

}
