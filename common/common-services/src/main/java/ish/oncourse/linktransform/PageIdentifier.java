package ish.oncourse.linktransform;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

public enum PageIdentifier {

	Home(new DefaultMatcher("/"), "ui/internal/Page"),
	/**
	 * courses/arts/drama Show course list page, optionally filtered by the
	 * subject tag identified by arts -> drama
	 */
	Courses("/courses", "ui/Courses"),
	/**
	 * Show products list page, no filtering available, only pagination
	 */
	Products("/products", "ui/Products"),
	/**
	 * course/ABC Show course detail for the course with code ABC
	 */
	Course("/course/", "ui/CourseDetails"),
	/**
	 * Voucher product detail for voucher product with provided id
	 */
	Product("/product/", "ui/ProductDetails"),
	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	CourseClass("/class/", "ui/CourseClassDetails"),
	/**
	 * page/123 This is always available for every webpage, even if it doesn't
	 * have a URL alias
	 */
	Page("/page/", "ui/internal/Page"),
	/**
	 * sites Show the site list for all sites
	 */
	Sites(new DefaultMatcher("/sites"), "ui/Sites"),
	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	Site("/site/", "ui/SiteDetails"),
	/**
	 * room/200 Show the room detail for the room with angel id of 200
	 */
	Room("/room/", "ui/RoomDetails"),
	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	Tutor("/tutor/", "ui/TutorDetails"),
	/**
	 * /sitemap.xml Google specific sitemap file.
	 */
	Sitemap(new DefaultMatcher("/sitemap.xml"), "ui/SitemapXML"),
	/**
	 * Path of the search autocomplete
	 */
	AdvancedKeyword(new DefaultMatcher("/advanced/keyword"), "ui/QuickSearchView"),
	/**
	 * Path of the suburbs autocomplete
	 */
	AdvancedSuburbs(new DefaultMatcher("/advanced/suburbs"), "ui/SuburbsTextArray"),
	/**
	 * Path of the refreshing the shortlist
	 */
	Shortlist(new DefaultMatcher("/refreshshortlist"), "ui/ShortListPage"),
	/**
	 * Path for the page of adding the discount.
	 */
	AddDiscount(new DefaultMatcher("/adddiscount"), "ui/AddDiscount"),

	/**
	 * Path for the page displaying promotions.
	 */
	Promotions(new DefaultMatcher("/promotions"), "ui/PromoCodesPage"),
	/**
	 * /Timeline/sessions?ids=123,456 Show the timeline view for the sessions of
	 * courseClasses with ids of 123 and 456
	 */
	Timeline(new DefaultMatcher("/timeline/sessions"), "ui/internal/TimelineData"),
	
	CoursesSitesMap(new DefaultMatcher("/coursessitesmap"), "ui/CoursesSitesMap"),

	ISHHealthCheck(new DefaultMatcher("/ishhealthcheck"), "ish/ISHHealthCheck"),

	PageNotFound("/pagenotfound", "ui/PageNotFound"),

	SiteNotFound("/sitenotfound", "ui/SiteNotFound");

	private Matcher matcher;
	private String pageName;

	private PageIdentifier(String pattern, String pageName) {
		this.matcher = new PrefixMatcher(pattern);
		this.pageName = pageName;
	}

	private PageIdentifier(Matcher matcher, String pageName) {
		this.matcher = matcher;
		this.pageName = pageName;
	}

	/**
	 * @return the pattern
	 */
	public Matcher getMatcher() {
		return matcher;
	}

	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * We need to use this method because we have to be sure that all PageIdentifier sorted by pattern length in desc.
	 * It needs becouse we should compore with longer patterns in first to exclude invalid choosing when two patterns prefixes equal
	 * each other:  courses and coursessitesmap
	 */
	public static PageIdentifier[] sortedValues()
	{
		PageIdentifier[] values = values();
		Arrays.sort(values, new Comparator<PageIdentifier>() {
			@Override
			public int compare(PageIdentifier o1, PageIdentifier o2) {
				return o2.matcher.getPattern().length() - o1.matcher.getPattern().length();
			}
		});
		return values;
	}

	public static PageIdentifier getPageIdentifierByPath(String path) {
		if (path.startsWith("/cms")) {
			path = path.replace("/cms", "");
		}

		for (PageIdentifier pageIdentifier : sortedValues()) {
			if (pageIdentifier.getMatcher().matches(path)) {
				return pageIdentifier;
			}
		}
		return PageNotFound;
	}

	private interface  Matcher
	{
		public boolean matches(String value);

		public String getPattern();
	}

	private static class DefaultMatcher implements Matcher
	{
		private String pattern;

		@Override
		public String getPattern() {
			return pattern;
		}

		private DefaultMatcher(String pattern) {
			this.pattern = pattern;
		}


		@Override
		public boolean matches(String value) {
			return value.equals(pattern);
		}
	}

	private static class PrefixMatcher implements Matcher
	{
		private String prefix;

		@Override
		public String getPattern() {
			return prefix;
		}

		private PrefixMatcher(String prefix) {
			this.prefix = prefix;
		}


		@Override
		public boolean matches(String value) {
			return value.startsWith(prefix);
		}
	}

	private static class RegexMatcher implements Matcher
	{
		private Pattern pattern;

		@Override
		public String getPattern() {
			return pattern.pattern();
		}

		private RegexMatcher(String patternRegex) {
			pattern = Pattern.compile(patternRegex);
		}


		@Override
		public boolean matches(String value) {
			return pattern.matcher(value).matches();
		}
	}
}
