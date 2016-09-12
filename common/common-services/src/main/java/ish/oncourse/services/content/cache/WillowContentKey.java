/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteLayout;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.PersistentObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class WillowContentKey {
	private ObjectId siteId;
	private ObjectId layoutId;
	private ObjectId objectId;
	private String elementName;

	public ObjectId getSiteId() {
		return siteId;
	}

	public ObjectId getLayoutId() {
		return layoutId;
	}

	public String getElementName() {
		return elementName;
	}

	public ObjectId getObjectId() {
		return objectId;
	}


	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	public static WillowContentKey valueOf(WebSite webSite, WebSiteLayout layout, PersistentObject object, String elementName) {
		WillowContentKey key = new WillowContentKey();
		key.siteId = webSite.getObjectId();
		key.layoutId = layout.getObjectId();
		key.objectId = object.getObjectId();
		key.elementName = elementName;
		return key;
	}
}
