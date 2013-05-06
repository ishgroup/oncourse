
package au.gov.training.services.organisation;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.2
 * 2013-05-06T18:49:01.337+03:00
 * Generated source version: 2.6.2
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage extends Exception {
    
    private au.gov.training.services.organisation.ValidationFault validationFault;

    public IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage() {
        super();
    }
    
    public IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage(String message, au.gov.training.services.organisation.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public IOrganisationServiceSearchByScopeValidationFaultFaultFaultMessage(String message, au.gov.training.services.organisation.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.organisation.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
