package ish.oncourse.linktransform;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

public enum PageIdentifier
{


	Home(new ExactMatcher("/"), "ui/internal/Page"),
	/**
	 * courses/arts/drama Show course list page, optionally filtered by the
	 * subject tag identified by arts -> drama
	 */
	Courses(new SlashedPrefixMatcher("/courses/"), "ui/Courses"),
	/**
	 * Show products list page, no filtering available, only pagination
	 */
	Products(new SlashedPrefixMatcher("/products/"), "ui/Products"),
	/**
	 * course/ABC Show course detail for the course with code ABC
	 */
	Course(new PrefixMatcher("/course/"), "ui/CourseDetails"),
	/**
	 * Voucher product detail for voucher product with provided id
	 */
	Product(new PrefixMatcher("/product/"), "ui/ProductDetails"),
	/**
	 * class/ABC-123 Show the class detail for the CourseClass with code ABC-123
	 */
	CourseClass(new PrefixMatcher("/class/"), "ui/CourseClassDetails"),
	/**
	 * page/123 This is always available for every webpage, even if it doesn't
	 * have a URL alias
	 */
	Page(new PrefixMatcher("/page/"), "ui/internal/Page"),
	/**
	 * sites Show the site list for all sites
	 */
	Sites(new SlashedPrefixMatcher("/sites/"), "ui/Sites"),
	/**
	 * site/200 Show the site detail for the site with angel id of 200
	 */
	Site(new PrefixMatcher("/site/"), "ui/SiteDetails"),
	/**
	 * room/200 Show the room detail for the room with angel id of 200
	 */
	Room(new PrefixMatcher("/room/"), "ui/RoomDetails"),
	/**
	 * tutor/123 Show the tutor detail for the tutor with angel id of 123
	 */
	Tutor(new PrefixMatcher("/tutor/"), "ui/TutorDetails"),
	/**
	 * /sitemap.xml Google specific sitemap file.
	 */
	Sitemap("/sitemap.xml", "ui/SitemapXML"),
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

	ISHHealthCheck("/ishhealthcheck", "ish/ISHHealthCheck"),

	PageNotFound("/pagenotfound", "ui/PageNotFound"),

	SiteNotFound("/sitenotfound", "ui/SiteNotFound");

	private  static final PageIdentifier[] sortedValues;
	static
	{
		sortedValues = sortedValues();
	}

	private Matcher matcher;
	private String pageName;

	private static final Logger LOGGER = Logger.getLogger(PageIdentifier.class);

	private PageIdentifier(String pattern, String pageName) {
		this.matcher = new ExactMatcher(pattern);
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
	private static PageIdentifier[] sortedValues()
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

		try {
			path = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn(e.getMessage(),e);
		}

		for (PageIdentifier pageIdentifier : sortedValues) {
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

	private static class ExactMatcher implements Matcher
	{
		private String pattern;

		@Override
		public String getPattern() {
			return pattern;
		}

		private ExactMatcher(String pattern) {
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
			return !value.equals(prefix) && value.startsWith(prefix);
		}
	}

	private static class SlashedPrefixMatcher implements Matcher
	{
		private String prefix;

		@Override
		public String getPattern() {
			return prefix;
		}

		private SlashedPrefixMatcher(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public boolean matches(String value) {
			value = value + '/';
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
