package ish.oncourse.linktransform;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.linktransform.PageIdentifier.*;
import static org.junit.Assert.assertEquals;

public class PageIdentifierTest {
	private Map<PageIdentifier, String[]> urls = new HashMap<>();
	{
		urls.put(Home, new String[]{"/"});
		urls.put(Courses, new String[]{"/courses/tag1", "/courses?start=10","/courses/tag1?start=10"});
		urls.put(Products, new String[]{"/products/tag1", "/products?start=10","/products/tag1?start=10"});
		urls.put(Course, new String[]{"/course/course1"});
		urls.put(Product, new String[]{"/product/product1"});
		urls.put(CourseClass, new String[]{"/class/course-class"});
		urls.put(Page, new String[]{"/page/121212"});
		urls.put(Sites, new String[]{"/sites"});
		urls.put(Site, new String[]{"/site/site1"});
		urls.put(Room, new String[]{"/room/1212"});
		urls.put(Tutor, new String[]{"/tutor/1212"});
		urls.put(Sitemap, new String[]{"/sitemap.xml"});
		urls.put(AdvancedKeyword, new String[]{"/advanced/keyword"});
		urls.put(AdvancedSuburbs, new String[]{"/advanced/suburbs"});
		urls.put(Shortlist,  new String[]{"/refreshshortlist"});
		urls.put(AddDiscount, new String[]{"/adddiscount"});
		urls.put(Promotions, new String[]{"/promotions"});
		urls.put(Timeline, new String[]{"/timeline/sessions"});
		urls.put(CoursesSitesMap, new String[]{"/coursessitesmap"});
		urls.put(ISHHealthCheck, new String[]{"/ishhealthcheck"});
		urls.put(SiteNotFound, new String[]{"/sitenotfound"});
		urls.put(PageNotFound, new String[]{"/pagenotfound"});
	}

	@Test
	public void test() {
		PageIdentifier[] pageIdentifiers = PageIdentifier.values();
		for (PageIdentifier pageIdentifier : pageIdentifiers) {
			String[] urls = getUrlBy(pageIdentifier);
			for (String url : urls) {
				assertEquals(String.format("Test pageIdentifier=%s and url=%s", pageIdentifier,url), pageIdentifier, PageIdentifier.getPageIdentifierByPath(url));
			}
		}


//		long time = System.currentTimeMillis();
//		String path1 = "/COURSES/Workskills/Computing?start=60&sites=1872125&loadedCoursesIds=1872157,5001820,1872172,1928337,1964008,5000850,1959449,1959457,5000689,1956392,1956403,1956406,1959245,1959276,1959312,5000690,5001882,5001883,5004449,1959482,1961607,5001099,1939109,1941575,1942077,1942097,1956366,1956367,1956369,1956372,1959430,1959438,1961723,1963252,1963268,5000687,5003907,5003377,1948576,1956294,1956360,1956364,5000671,5001181,1955502,1955515,1959346,5000691,1956357,1963289,5000688,2223475,1955531,1959285,1959300,1961671,1961722,5000695,1956353,1956354";
//		String path = "/tutor/47nfo.asp?id=973=Kang_Q_and_A&sca=%C4%DA%BE%EE9412.com&toname=qixtqtovgBooking_Planning_and_Vacations/3962/yjia=1033%bd%ec%a7%80%ed%98%9c-%ec%84%9c%ed%95%98-%ec%9e%84%eb%8f%99%ed%98%b8s_main1=291XgkxnPvCMG3lGvph0LpRS+hWVIBWjlUVL7SDEyBlxQJzalhCgymdJgQMw1xBFAmGSK6HIPjgSkYBmAaOFWpGc1CEECxFU5rJgzpJ4lG7RO5O8jsNLm0NuCAVE89uuxCmiFDO7WT1B7xKaUkszys/aDWcatELbfb8mE+E0Nci7St7KH28505H2FRIaSiMR07EiTxn6B+LcR8tMDDXP0ucmwejvqk3EnI+WCIKC4WxiBvezavdVsLZoMbjJgqKX3F3JFOFCQIUejyA-bzv5zO3x10x02x11p4x2X12x01w1u8z8p3x0X14x18x0X14o3w8w1p3p9p3p3x0X14x18x0X14z8p2x01q9p4x2X12x01w1u9z8p4q7q8x08x01o8q7x09x01w1w6x1X1X07q5w7x08q7x15x15z8p2x1X19o3w8w1v6v6v3u9v5v5u8v0vV3v3v1v3vS8v2v2v0v3v3v1u9u8v4u9v7v3v5u8v6u8u8v2v6v3u9u9v5v5u8vV0vV3v5v3v1v3v5v1u9u8v4u9v7v3v5u8v6u8v5v7vV3v6v6v3u8v7v5v1v3u9u8vV0u8u9u9v3u8v3u9v0v1v7u8u9v1v0v5v3u9vS9v4u8u9v3u8v2v3u9v3v7v1u8u9vV6z8w8q7x16q7p4q7q8x08x01o8q7x09x01w1w6x1X1X07q5w8q7x16q7z8w8q7x16q7p3x0X14x18x0X14o3w8w1p3x0X14x18x0X14w8x11q9html%A2%E0%B8%99%E0%B9%82%E0%B8%99%E0%B8%99%E0%B8%AA%E0%B8%B0%E0%B8%AD%E0%B8%B2%E0%B8%94%E0%B8%A7%E0%B8%B4%E0%B8%97%E0%B8%A2%E0%B8%B2%E0%B8%84%E0%B8%B2%E0%B8%A3.htmlB8%A3.html0%B8%AA%E0%B8%B5%E0%B8%A2.htmlA%E0%B8%AD%E0%B8%AD%E0%B8%81%E0%B9%81%E0%B8%9A%E0%B8%9A%E0%B9%81%E0%B8%A5%E0%B8%B0%E0%B8%95%E0%B8%B4%E0%B8%94%E0%B8%95%E0%B8%B1%E0%B9%89%E0%B8%87%E0%B8%A3%E0%B8%B0%E0%B8%9A%E0%B8%9A%E0%B8%84%E0%B8%A7%E0%B8%9A%E0%B8%84%E0%B8%B8%E0.html5%AD%A6%E7%94%9F%E7%A0%94%E7%A9%B6%E5%84%AA%E7%A7%80%E7%99%BA%E8%A1%A8%E8%B3%9E%E3%82%92%E5%8F%93%9E%E3%82%92%E5%8F%97%E8%B3%9E%5B%E9%9B%BB%E6%B0%97%E9%80%9A%E4%BF%A1%E5%A4%A7%E5%AD%A6+%E6%83%85%E5%A0%B1%E7%90%86%E5%B7%A5%E5%AD%A6%E9%83%A8%5DB8%B1%E0%B8%A1%E0%B8%A1%E0%B8%99%E0%B8%B2_%E0%B8%9A%E0%B8%A3%E0%B8%B4%E0%B8%AB%E0%B8%B2%E0%B8%A3%E0%B8%88%E0%B8%B1%E0%B8%94%E0%B8%81%E0%B8%B2%E0%B8%A3.htmlview=jumbohtml0%B8%A1.htmlid=24835&goto=lastpostE0%B8%AA%E0%B8%B8%E0%B8%82%E0%B8%A0%E0%B8%B2%E0%B8%9E.html";
//
//		PageIdentifier page = PageIdentifier.getPageIdentifierByPath(path);
//		System.out.println(page);
//		time = System.currentTimeMillis() - time;
//		System.out.println(time);
//
//		time = System.currentTimeMillis();
//		String[] strings = StringUtils.split(path, '/');
//		if (strings.length > 0)
//		strings[0].equals("tutor");
//		time = System.currentTimeMillis() - time;
//		System.out.println(time);

	}

	private String[] getUrlBy(PageIdentifier pageIdentifier) {
		String[] value = urls.get(pageIdentifier);
		return value == null ? new String[]{StringUtils.EMPTY}: value;
	}
}
