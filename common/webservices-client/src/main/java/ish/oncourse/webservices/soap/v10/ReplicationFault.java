
package ish.oncourse.webservices.soap.v10;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2018-02-13T16:01:30.808+03:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "faultResponse", targetNamespace = "http://repl.v10.soap.webservices.oncourse.ish/")
public class ReplicationFault extends Exception {
    
    private ish.oncourse.webservices.v10.stubs.replication.FaultReason faultResponse;

    public ReplicationFault() {
        super();
    }
    
    public ReplicationFault(String message) {
        super(message);
    }
    
    public ReplicationFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v10.stubs.replication.FaultReason faultResponse) {
        super(message);
        this.faultResponse = faultResponse;
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v10.stubs.replication.FaultReason faultResponse, Throwable cause) {
        super(message, cause);
        this.faultResponse = faultResponse;
    }

    public ish.oncourse.webservices.v10.stubs.replication.FaultReason getFaultInfo() {
        return this.faultResponse;
    }
}
