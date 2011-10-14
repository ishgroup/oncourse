
package au.gov.training.services.trainingcomponent;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.4.0
 * 2011-10-12T16:55:20.952+03:00
 * Generated source version: 2.4.0
 * 
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage extends Exception {
    public static final long serialVersionUID = 20111012165520L;
    
    private au.gov.training.services.trainingcomponent.ValidationFault validationFault;

    public ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage() {
        super();
    }
    
    public ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.trainingcomponent.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
