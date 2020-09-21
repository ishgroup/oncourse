
package org.w3._2001.xmlschema;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter4
    extends XmlAdapter<String, Boolean>
{


    public Boolean unmarshal(String value) {
        return ((boolean)javax.xml.bind.DatatypeConverter.parseBoolean(value));
    }

    public String marshal(Boolean value) {
        if (value == null) {
            return null;
        }
        return (javax.xml.bind.DatatypeConverter.printBoolean((boolean)(boolean)value));
    }

}
