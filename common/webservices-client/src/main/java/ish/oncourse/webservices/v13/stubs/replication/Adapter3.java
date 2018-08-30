
package ish.oncourse.webservices.v13.stubs.replication;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter3
    extends XmlAdapter<String, Long>
{


    public Long unmarshal(String value) {
        return ((long)javax.xml.bind.DatatypeConverter.parseLong(value));
    }

    public String marshal(Long value) {
        if (value == null) {
            return null;
        }
        return (javax.xml.bind.DatatypeConverter.printLong((long)(long)value));
    }

}
