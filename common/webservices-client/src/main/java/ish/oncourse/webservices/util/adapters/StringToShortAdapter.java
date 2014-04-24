/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.util.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringToShortAdapter extends XmlAdapter<String, Short> {

	public Short unmarshal(String value) {
		return (javax.xml.bind.DatatypeConverter.parseShort(value));
	}

	public String marshal(Short value) {
		if (value == null) {
			return null;
		}
		return (javax.xml.bind.DatatypeConverter.printShort(value));
	}
}
