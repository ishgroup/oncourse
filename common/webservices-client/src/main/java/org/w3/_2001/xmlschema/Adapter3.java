
package org.w3._2001.xmlschema;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter3
		extends XmlAdapter<String, Short>
{


	public Short unmarshal(String value) {
		return ((short)javax.xml.bind.DatatypeConverter.parseShort(value));
	}

	public String marshal(Short value) {
		if (value == null) {
			return null;
		}
		return (javax.xml.bind.DatatypeConverter.printShort((short)(short)value));
	}

}
