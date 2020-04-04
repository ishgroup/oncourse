
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.3.5
 * 2020-03-27T13:59:32.258+11:00
 * Generated source version: 3.3.5
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle extends Exception {

    private au.gov.usi._2018.ws.ErrorInfo errorInfo;

    public IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle() {
        super();
    }

    public IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }

    public IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2018.ws.ErrorInfo errorInfo, Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2018.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
