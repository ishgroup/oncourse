
package ish.oncourse.webservices.v9.stubs.replication;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter5
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
