package ish.oncourse.linktransform;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.linktransform.PageIdentifier.*;
import static org.junit.Assert.*;

public class PageIdentifierTest {
	private Map<PageIdentifier, Urls> urls = new HashMap<>();
	{
		Urls value =  new Urls();
		value.setValids("/","/ ");
		value.setInvalids("/asdasd/", "/asdasd ", "/asdasd/");
		urls.put(Home, value);

		value =  new Urls();
		value.setValids("/courses",
				"/courses/",
				"/courses/tag1",
				"/courses/tag1/",
				"/courses ",
				"/courses/ ",
				"/courses/tag1 ",
				"/courses/tag1/ ",
				"/courses?start=10",
				"/courses/tag1?start=10",
				"/courses/Subjects?start=250&sites=4920,4935,136082,2203740,4975,163652,4970,4942,4963,4940,4960,139972,4957,4956,4966,4983,4930&loadedCoursesIds=5355,5003360,5003366,5001868,5161,5219,5472,5350,1224027,5275",
				"/courses/lifestyle+and+leisure/tap+into+your+potential",
				"/courses/courses+for+everyone?start=70&sites=1346774,5000818,5000788,5000792,5000795,5000796,5000791,5000801,5000775,5001094,5000786,5000777&loadedCoursesIds=5001585,5002077,2254063,5004747,5004863,5004913,5004950,5004951,5004956,5004957",
				"/courses/lifestyle/horse+riding?addShortlist=1101062"
		);
		value.setInvalids("/coursess", "/coursess/", "/coursess/ ");
		urls.put(Courses, value);

		value =  new Urls();
		value.setValids("/products",
				"/products/",
				"/products/tag1",
				"/products/tag1/",
				"/products ",
				"/products/ ",
				"/products/tag1 ",
				"/products/tag1/ ",
				"/products?start=10",
				"/products/tag1?start=10",
				"/products/Subjects?start=250&sites=4920,4935,136082,2203740,4975,163652,4970,4942,4963,4940,4960,139972,4957,4956,4966,4983,4930&loadedCoursesIds=5355,5003360,5003366,5001868,5161,5219,5472,5350,1224027,5275",
				"/products/lifestyle+and+leisure/tap+into+your+potential",
				"/products/courses+for+everyone?start=70&sites=1346774,5000818,5000788,5000792,5000795,5000796,5000791,5000801,5000775,5001094,5000786,5000777&loadedCoursesIds=5001585,5002077,2254063,5004747,5004863,5004913,5004950,5004951,5004956,5004957",
				"/products/lifestyle/horse+riding?addShortlist=1101062"
		);
		value.setInvalids("/productss", "/productss/", "/productss/ ");
		urls.put(Products, value);

		value =  new Urls();
		value.setValids("/course/course1",
				"/course/course1 ",
				"/course/course1/",
				"/course/course1/ "
		);
		value.setInvalids("/course", "/course ");
		urls.put(Course, value);


		value =  new Urls();
		value.setValids("/product/product1",
				"/product/product1 ",
				"/product/product1/",
				"/product/product1/ ",
				"/product/product1/one"
		);
		value.setInvalids("/product", "/product ","/product/");
		urls.put(Product, value);

		value =  new Urls();
		value.setValids("/class/course-class",
				"/class/course-class ",
				"/class/course-class/",
				"/class/course-class/ ",
				"/class/course-class/one"
		);
		value.setInvalids("/class", "/class ","/class/");
		urls.put(CourseClass, value);

		value =  new Urls();
		value.setValids("/page/123",
				"/page/123 ",
				"/page/123/",
				"/page/123/ ",
				"/page/123/one"
		);
		value.setInvalids("/page", "/page ", "/page /12344", "/page/");
		urls.put(Page, value);

		value =  new Urls();
		value.setValids("/sites",
				"/sites/",
				"/sites/ ",
				"/sites ",
				"/sites/one"
		);
		value.setInvalids("/sitess", "/sitess/","/sitess/ ","/sitess /");
		urls.put(Sites, value);

		value =  new Urls();
		value.setValids("/site/1234",
				"/site/1234/",
				"/site/1234 ",
				"/site/1234/ "
		);
		value.setInvalids("/site", "/site/","/site/ ");
		urls.put(Site, value);

		value =  new Urls();
		value.setValids("/room/1234",
				"/room/1234/",
				"/room/1234 ",
				"/room/1234/ "
		);
		value.setInvalids("/room", "/room/","/room/ ");
		urls.put(Room, value);

		value =  new Urls();
		value.setValids("/tutor/1234",
				"/tutor/1234/",
				"/tutor/1234 ",
				"/tutor/1234/ "
		);
		value.setInvalids("/tutor", "/tutor/","/tutor/ ");
		urls.put(Tutor, value);

		value =  new Urls();
		value.setValids("/waitinglistform/1234",
				"/waitinglistform/1234 "
				
		);
		value.setInvalids("/waitinglistform","/waitinglistform/", "/waitinglistform /1234");
		
		urls.put(WaitingListForm, value);
	
		urls.put(Sitemap, forExectMattcher("/sitemap.xml",true));

		urls.put(AdvancedKeyword, forExectMattcher("/advanced/keyword",true));

		urls.put(AdvancedSuburbs, forExectMattcher("/advanced/suburbs",true));

		urls.put(Shortlist, forExectMattcher("/refreshshortlist",true));

		urls.put(AddDiscount, forExectMattcher("/adddiscount",true));

		urls.put(Promotions, forExectMattcher("/promotions",true));

		urls.put(Timeline, forExectMattcher("/timeline/sessions",true));

		urls.put(CoursesSitesMap, forExectMattcher("/coursessitesmap",true));

		urls.put(ISHHealthCheck, forExectMattcher("/ishhealthcheck",true));

		urls.put(SiteNotFound, forExectMattcher("/sitenotfound",true));

		urls.put(PageNotFound, forExectMattcher("/pagenotfound",false));

	}

	private static Urls forExectMattcher(String prefix, boolean withInvalid)
	{
		Urls value =  new Urls();
		value.setValids(String.format("%s",prefix),
				String.format("%s ",prefix)
		);
		if (withInvalid)
		value.setInvalids(String.format("%s/",prefix),
				String.format("%s/ ",prefix),
				String.format("%s/one",prefix));
		return value;

	}


	@Test
	public void test() throws MalformedURLException {

		URL url1 = new URL("http://localhost/coursesHello/");

		PageIdentifier[] pageIdentifiers = PageIdentifier.values();
		for (PageIdentifier pageIdentifier : pageIdentifiers) {
			Urls urls = getUrlsBy(pageIdentifier);
			assertNotNull(String.format("Urls for %s is defined", pageIdentifier), urls);
			for (URL url : urls.valids) {
				assertEquals(String.format("Test valid pageIdentifier=%s and url=%s", pageIdentifier,url), pageIdentifier, PageIdentifier.getPageIdentifierByPath(url.getPath()));
			}

			for (URL url : urls.invalids) {
				assertNotEquals(String.format("Test invalid pageIdentifier=%s and url=%s", pageIdentifier, url), pageIdentifier, PageIdentifier.getPageIdentifierByPath(url.getPath()));
			}
		}
	}

	private Urls getUrlsBy(PageIdentifier pageIdentifier) {
		return urls.get(pageIdentifier);
	}




	private static class Urls
	{
		private List<URL> valids = new ArrayList<>();
		private List<URL> invalids = new ArrayList<>();

		void setValids(String... valids) {
			for (String valid : valids) {
				this.valids.add(getURLBy(valid));

			}
		}

		void setInvalids(String... invalids)
		{
			for (String valid : invalids) {
				this.invalids.add(getURLBy(valid));
			}
		}

		public URL getURLBy(String path)
		{
			try {
				return new URL(String.format("http://localhost%s", path));
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException();
			}
		}
	}


}
