
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2020-04-04T01:49:03.274+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ArrayOfErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage extends Exception {
    
    private au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo;

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage() {
        super();
    }
    
    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage(String message) {
        super(message);
    }
    
    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo) {
        super(message);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo, Throwable cause) {
        super(message, cause);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public au.gov.usi._2018.ws.ArrayOfErrorInfo getFaultInfo() {
        return this.arrayOfErrorInfo;
    }
}
