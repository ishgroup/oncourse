
package ish.oncourse.webservices.soap.v8;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.1
 * 2014-10-21T18:49:07.993+03:00
 * Generated source version: 2.6.1
 */

@WebFault(name = "faultReason", targetNamespace = "http://repl.v8.soap.webservices.oncourse.ish/")
public class ReplicationFault extends Exception {
    
    private ish.oncourse.webservices.v8.stubs.replication.FaultReason faultReason;

    public ReplicationFault() {
        super();
    }
    
    public ReplicationFault(String message) {
        super(message);
    }
    
    public ReplicationFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v8.stubs.replication.FaultReason faultReason) {
        super(message);
        this.faultReason = faultReason;
    }

    public ReplicationFault(String message, ish.oncourse.webservices.v8.stubs.replication.FaultReason faultReason, Throwable cause) {
        super(message, cause);
        this.faultReason = faultReason;
    }

    public ish.oncourse.webservices.v8.stubs.replication.FaultReason getFaultInfo() {
        return this.faultReason;
    }
}
