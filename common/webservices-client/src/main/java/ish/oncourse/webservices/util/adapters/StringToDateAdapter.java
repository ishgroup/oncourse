/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.util.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class StringToDateAdapter extends XmlAdapter<String, Date> {

	public Date unmarshal(String value) {
		return (org.apache.cxf.tools.common.DataTypeAdapter.parseDateTime(value));
	}

	public String marshal(Date value) {
		return (org.apache.cxf.tools.common.DataTypeAdapter.printDateTime(value));
	}
}
