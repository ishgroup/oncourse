
package au.gov.usi._2015.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2016-11-08T21:50:02.105+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2015/ws")
public class IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle extends Exception {
    
    private au.gov.usi._2015.ws.ErrorInfo errorInfo;

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle() {
        super();
    }
    
    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }
    
    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2015.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2015.ws.ErrorInfo errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2015.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
