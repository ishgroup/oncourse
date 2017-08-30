
package ish.oncourse.webservices.soap.v13;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2017-08-30T21:30:01.252+10:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "errorCode", targetNamespace = "http://repl.v13.soap.webservices.oncourse.ish/")
public class AuthFailure extends Exception {
    
    private ish.oncourse.webservices.v13.stubs.replication.ErrorCode errorCode;

    public AuthFailure() {
        super();
    }
    
    public AuthFailure(String message) {
        super(message);
    }
    
    public AuthFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFailure(String message, ish.oncourse.webservices.v13.stubs.replication.ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthFailure(String message, ish.oncourse.webservices.v13.stubs.replication.ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ish.oncourse.webservices.v13.stubs.replication.ErrorCode getFaultInfo() {
        return this.errorCode;
    }
}
