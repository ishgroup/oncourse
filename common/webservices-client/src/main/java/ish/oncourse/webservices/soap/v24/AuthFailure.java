
package ish.oncourse.webservices.soap.v24;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.2.6
 * 2021-10-19T20:57:17.516+11:00
 * Generated source version: 3.2.6
 */

@WebFault(name = "errorCode", targetNamespace = "http://repl.v24.soap.webservices.oncourse.ish/")
public class AuthFailure extends Exception {

    private ish.oncourse.webservices.v24.stubs.replication.ErrorCode errorCode;

    public AuthFailure() {
        super();
    }

    public AuthFailure(String message) {
        super(message);
    }

    public AuthFailure(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public AuthFailure(String message, ish.oncourse.webservices.v24.stubs.replication.ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthFailure(String message, ish.oncourse.webservices.v24.stubs.replication.ErrorCode errorCode, java.lang.Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ish.oncourse.webservices.v24.stubs.replication.ErrorCode getFaultInfo() {
        return this.errorCode;
    }
}
