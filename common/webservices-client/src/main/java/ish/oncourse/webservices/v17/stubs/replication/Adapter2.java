
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class Adapter2
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (org.apache.cxf.tools.common.DataTypeAdapter.parseDateTime(value));
    }

    public String marshal(Date value) {
        return (org.apache.cxf.tools.common.DataTypeAdapter.printDateTime(value));
    }

}
