
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2019-10-26T00:33:44.854+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle extends Exception {
    
    private au.gov.usi._2018.ws.ErrorInfo errorInfo;

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle() {
        super();
    }
    
    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }
    
    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2018.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
