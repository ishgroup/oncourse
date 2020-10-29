
package ish.oncourse.webservices.v7.stubs.reference;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        if (value == null) {
            return null;
        }
        return DatatypeConverter.parseDateTime(value).getTime();
    }

    public String marshal(Date value) {
        if (value == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(value);
        return DatatypeConverter.printDateTime(c);
    }

}
