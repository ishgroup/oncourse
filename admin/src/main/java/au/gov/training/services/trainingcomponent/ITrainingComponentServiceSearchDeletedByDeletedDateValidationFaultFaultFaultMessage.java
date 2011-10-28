
package au.gov.training.services.trainingcomponent;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.4.0
 * 2011-10-28T17:30:39.797+03:00
 * Generated source version: 2.4.0
 * 
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage extends Exception {
    public static final long serialVersionUID = 20111028173039L;
    
    private au.gov.training.services.trainingcomponent.ValidationFault validationFault;

    public ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage() {
        super();
    }
    
    public ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.trainingcomponent.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
