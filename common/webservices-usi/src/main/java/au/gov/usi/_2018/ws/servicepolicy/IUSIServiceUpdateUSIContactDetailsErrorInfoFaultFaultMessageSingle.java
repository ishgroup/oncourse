
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2020-04-04T01:49:03.264+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle extends Exception {
    
    private au.gov.usi._2018.ws.ErrorInfo errorInfo;

    public IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle() {
        super();
    }
    
    public IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }
    
    public IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2018.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
