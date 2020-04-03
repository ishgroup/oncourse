
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2020-04-04T01:49:03.252+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle extends Exception {
    
    private au.gov.usi._2018.ws.ErrorInfo errorInfo;

    public IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle() {
        super();
    }
    
    public IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }
    
    public IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2018.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
