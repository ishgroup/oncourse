
package ish.oncourse.webservices.soap.v6;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.1
 * 2014-04-23T17:58:27.371+03:00
 * Generated source version: 2.6.1
 */

@WebFault(name = "errorCode", targetNamespace = "http://repl.v6.soap.webservices.oncourse.ish/")
public class AuthFailure extends Exception {
    
    private ish.oncourse.webservices.v6.stubs.replication.ErrorCode errorCode;

    public AuthFailure() {
        super();
    }
    
    public AuthFailure(String message) {
        super(message);
    }
    
    public AuthFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFailure(String message, ish.oncourse.webservices.v6.stubs.replication.ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthFailure(String message, ish.oncourse.webservices.v6.stubs.replication.ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ish.oncourse.webservices.v6.stubs.replication.ErrorCode getFaultInfo() {
        return this.errorCode;
    }
}
