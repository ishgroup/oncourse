
package ish.oncourse.webservices.soap.v14;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.16
 * 2017-08-30T21:30:03.304+10:00
 * Generated source version: 2.6.16
 */

@WebFault(name = "faultResponse", targetNamespace = "http://repl.v14.soap.webservices.oncourse.ish/")
public class ReplicationFault extends Exception {
    
    private ish.oncourse.webservices.v14.stubs.replication.FaultReason faultResponse;

    public ReplicationFault() {
        super();
    }
    
    public ReplicationFault(String message) {
        super(message);
    }
    
    public ReplicationFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v14.stubs.replication.FaultReason faultResponse) {
        super(message);
        this.faultResponse = faultResponse;
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v14.stubs.replication.FaultReason faultResponse, Throwable cause) {
        super(message, cause);
        this.faultResponse = faultResponse;
    }

    public ish.oncourse.webservices.v14.stubs.replication.FaultReason getFaultInfo() {
        return this.faultResponse;
    }
}
