/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.structure.Page;

import java.util.Set;

/**
 * User: akoiro
 * Date: 23/11/17
 */
public class PageSourceImpl implements PageSource {

	private final PageLoadService pageLoadService;

	public PageSourceImpl(PageLoadService pageLoadService) {
		this.pageLoadService = pageLoadService;
	}

	@Override
	public void clearCache() {
		pageLoadService.clean();
	}

	@Override
	public Page getPage(String canonicalPageName) {
		return pageLoadService.getPage(canonicalPageName);
	}

	/**
	 * We don't use this functionality.
	 */
	public Set<Page> getAllPages() {
		return pageLoadService.getAllPages();
	}
}
