
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2018-12-26T20:06:24.353+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ArrayOfErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage extends Exception {
    
    private au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo;

    public IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage() {
        super();
    }
    
    public IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage(String message) {
        super(message);
    }
    
    public IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo) {
        super(message);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo, Throwable cause) {
        super(message, cause);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public au.gov.usi._2018.ws.ArrayOfErrorInfo getFaultInfo() {
        return this.arrayOfErrorInfo;
    }
}
