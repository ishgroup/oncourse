
package ish.oncourse.webservices.v18.stubs.replication;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter4
    extends XmlAdapter<String, Integer>
{


    public Integer unmarshal(String value) {
        return ((int)javax.xml.bind.DatatypeConverter.parseInt(value));
    }

    public String marshal(Integer value) {
        if (value == null) {
            return null;
        }
        return (javax.xml.bind.DatatypeConverter.printInt((int)(int)value));
    }

}
