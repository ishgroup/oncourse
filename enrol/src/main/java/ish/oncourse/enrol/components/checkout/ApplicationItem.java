/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.components.checkout;

import ish.oncourse.model.Application;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ApplicationItem {
	@Parameter(required = true)
	@Property
	private Application application;

	@Parameter(required = true)
	@Property
	private Integer contactIndex;

	@Parameter(required = true)
	@Property
	private Integer applicationIndex;

	@Parameter(required = true)
	@Property
	private ApplicationItemDelegate delegate;

	@Property
	@Parameter(required = true)
	private Boolean checked;

	@Property
	@Parameter(required = false)
	private String error;

	@Inject
	private Request request;

	@Parameter(required = false)
	private Block blockToRefresh;


	public Integer[] getActionContext() {
		return new Integer[]{contactIndex, applicationIndex};
	}

	public String getApplicationClass() {
		return checked ? StringUtils.EMPTY: "disabled";
	}


	@OnEvent(value = "selectApplicationEvent")
	public Object selectApplication(Integer contactIndex, Integer applicationIndex) {
		if (!request.isXHR())
			return null;
		if (delegate != null) {
			delegate.onChange(contactIndex, applicationIndex);
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}


	public static interface ApplicationItemDelegate {
		public void onChange(Integer contactIndex, Integer applicationIndex);
	}
}
