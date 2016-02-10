package ish.oncourse.cms.components;

import ish.oncourse.linktransform.URLPath;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class RedirectsSettingsTest {

	private String[] testpaths = {
			"/adult?utm_source=Admatic%20&utm_medium=banners&utm_campaign=250x250%20Retarget%20Adult",
			"/rcg%20&%20rsg/rcg%20&%20rsg",
			"/bar%20&%20cocktail%20skills",
			"/catering%20&%20machine%20rental",
			"/courses/Computer%20&%20IT",
			"/courses/computer%20&%20it/web%20design",
			"/courses/computer+&amp%3B+it/digital+marketing",
			"/courses/computer+&amp%3B+it/e-commerce",
			"/courses/Languages%20&%20Culture",
			"/courses/languages+&+culture%3Cbr%20/%3E"
	};

	@Test
	public void test() throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
		for (String testpath : testpaths) {
			URLPath path = URLPath.valueOf(testpath);
			System.out.println(path.getDecodedPath());
			System.out.println(testpath);
			System.out.println(path.getEncodedPath());
		}

		String inputPath = "/rcg%20&%20rsg/rcg%20&%20rsg";

		URLPath path = URLPath.valueOf(inputPath);

		assertEquals("/rcg & rsg/rcg & rsg", path.getDecodedPath());
		assertEquals("/rcg+%26+rsg/rcg+%26+rsg", path.getEncodedPath());

	}


}
