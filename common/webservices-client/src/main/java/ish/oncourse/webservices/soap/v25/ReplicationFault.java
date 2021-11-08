
package ish.oncourse.webservices.soap.v25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.2.6
 * 2021-11-09T12:34:11.973+03:00
 * Generated source version: 3.2.6
 */

@WebFault(name = "faultResponse", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
public class ReplicationFault extends Exception {

    private ish.oncourse.webservices.v25.stubs.replication.FaultReason faultResponse;

    public ReplicationFault() {
        super();
    }

    public ReplicationFault(String message) {
        super(message);
    }

    public ReplicationFault(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v25.stubs.replication.FaultReason faultResponse) {
        super(message);
        this.faultResponse = faultResponse;
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v25.stubs.replication.FaultReason faultResponse, java.lang.Throwable cause) {
        super(message, cause);
        this.faultResponse = faultResponse;
    }

    public ish.oncourse.webservices.v25.stubs.replication.FaultReason getFaultInfo() {
        return this.faultResponse;
    }
}
