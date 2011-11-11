package ish.oncourse.linktransform;

import java.util.regex.Pattern;

public enum PageIdentifier {

	Home("/", "ui/internal/Page"),
	/**
	 * courses/arts/drama Show course list page, optionally filtered by the
	 * subject tag identified by arts -> drama
	 */
	Courses("/courses(/(.+)*)*", "ui/Courses"),
	/**
	 * course/ABC Show course detail for the cource with code ABC
	 */
	Course("/course/((\\w|\\s)+)(/)?", "ui/CourseDetails"),
	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	CourseClass("/class/((\\w|\\s)+)-((\\w|\\s)+)(/)?", "ui/CourseClassDetails"),
	/**
	 * page/123 This is always available for every webpage, even if it doesn't
	 * have a URL alias
	 */
	Page("/page/(\\d+)(/)?", "ui/internal/Page"),
	/**
	 * sites Show the site list for all sites
	 */
	Sites("/sites(/)?", "ui/Sites"),
	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	Site("/site/(.+)(/)?", "ui/SiteDetails"),
	/**
	 * room/200 Show the room detail for the room with angel id of 200
	 */
	Room("/room/(\\d+)(/)?", "ui/RoomDetails"),
	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	Tutor("/tutor/(\\d+)(/)?", "ui/TutorDetails"),
	/**
	 * /sitemap.xml Google specific sitemap file.
	 */
	Sitemap("/sitemap\\.xml(/)?", "ui/SitemapXML"),
	/**
	 * Path of the search autocomplete
	 */
	AdvancedKeyword("/advanced/keyword", "ui/QuickSearchView"),
	/**
	 * Path of the suburbs autocomplete
	 */
	AdvancedSuburbs("/advanced/suburbs", "ui/SuburbsTextArray"),
	/**
	 * Path of the refreshing the shortlist
	 */
	Shortlist("/refreshshortlist", "ui/ShortListPage"),
	/**
	 * Path for the page of adding the discount.
	 */
	AddDiscount("/adddiscount", "ui/AddDiscount"),

	/**
	 * Path for the page displaying promotions.
	 */
	Promotions("/promotions", "ui/PromoCodesPage"),
	/**
	 * /Timeline/sessions?ids=123,456 Show the timeline view for the sessions of
	 * courseClasses with ids of 123 and 456
	 */
	Timeline("/timeline/sessions", "ui/internal/TimelineData"),
	
	CoursesSitesMap("/coursessitesmap", "ui/CoursesSitesMap"),

	PageNotFound(".", "ui/PageNotFound"),

	SiteNotFound(".", "ui/SiteNotFound");

	private Pattern pattern;
	private String pageName;

	private PageIdentifier(String patternRegex, String pageName) {
		if (patternRegex != null) {
			pattern = Pattern.compile(patternRegex);
		}
		this.pageName = pageName;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}

	public static PageIdentifier getPageIdentifierByPath(String path) {
		if (path.startsWith("/cms")) {
			path = path.replace("/cms", "");
		}
		for (PageIdentifier pageIdentifier : values()) {
			if (pageIdentifier.getPattern().matcher(path).matches()) {
				return pageIdentifier;
			}
		}
		return PageNotFound;
	}
}
