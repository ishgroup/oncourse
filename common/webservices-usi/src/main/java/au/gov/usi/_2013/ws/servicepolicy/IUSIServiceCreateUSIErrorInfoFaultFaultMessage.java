
package au.gov.usi._2013.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.13
 * 2014-10-24T13:04:25.074+03:00
 * Generated source version: 2.7.13
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2013/ws")
public class IUSIServiceCreateUSIErrorInfoFaultFaultMessage extends Exception {
    
    private au.gov.usi._2013.ws.ErrorInfoType errorInfo;

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage() {
        super();
    }
    
    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message) {
        super(message);
    }
    
    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, au.gov.usi._2013.ws.ErrorInfoType errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, au.gov.usi._2013.ws.ErrorInfoType errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2013.ws.ErrorInfoType getFaultInfo() {
        return this.errorInfo;
    }
}
