
package org.w3._2001.xmlschema;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, Long>
{


    public Long unmarshal(String value) {
        return (ish.oncourse.webservices.util.LongAdapter.parseLong(value));
    }

    public String marshal(Long value) {
        return (ish.oncourse.webservices.util.LongAdapter.printLong(value));
    }

}
