
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2019-10-26T00:33:44.788+11:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "ArrayOfErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceGetCountriesErrorInfoFaultFaultMessage extends Exception {
    
    private au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo;

    public IUSIServiceGetCountriesErrorInfoFaultFaultMessage() {
        super();
    }
    
    public IUSIServiceGetCountriesErrorInfoFaultFaultMessage(String message) {
        super(message);
    }
    
    public IUSIServiceGetCountriesErrorInfoFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceGetCountriesErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo) {
        super(message);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public IUSIServiceGetCountriesErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo, Throwable cause) {
        super(message, cause);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public au.gov.usi._2018.ws.ArrayOfErrorInfo getFaultInfo() {
        return this.arrayOfErrorInfo;
    }
}
